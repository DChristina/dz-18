package api;
import io.restassured.http.ContentType;
import org.testng.Assert;
import org.testng.annotations.Test;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Random;

import static io.restassured.RestAssured.given;

public class BookerTest {
    private final static String URL = "https://restful-booker.herokuapp.com/";
    @Test
    public void authorizationTest(){
        Specification.installSpecification(Specification.requestSpec(URL),Specification.responseSpecGood());
        AuthorizationUser admin = new AuthorizationUser("admin", "password123");
        int expectedTokenSize  = 15;
        SuccessfulAuthorization authorization = given()
                .header("Accept", "application/json")
                .body(admin)
                .when()
                .post("auth")
                .then().log().all()
                .extract().as(SuccessfulAuthorization.class);

        Assert.assertNotNull(authorization.getToken());
        Assert.assertEquals(authorization.getToken().length(),expectedTokenSize);
    }

    @Test
    public void creationBookingTest(){
        Specification.installSpecification(Specification.requestSpec(URL),Specification.responseSpecGood());

        Booking booking = given()
                .header("Accept", "application/json")
                .body("{\n" +
                        "    \"firstname\" : \"Jim\",\n" +
                        "    \"lastname\" : \"Brown\",\n" +
                        "    \"totalprice\" : 111,\n" +
                        "    \"depositpaid\" : true,\n" +
                        "    \"bookingdates\" : {\n" +
                        "        \"checkin\" : \"2018-01-01\",\n" +
                        "        \"checkout\" : \"2019-01-01\"\n" +
                        "    },\n" +
                        "    \"additionalneeds\" : \"Breakfast\"\n" +
                        "}")
                .when()
                .post("booking")
                .then().log().all()
                .extract().body().jsonPath().getObject("booking", Booking.class);

        Assert.assertEquals(booking.getFirstname(),"Jim");
        Assert.assertEquals(booking.getLastname(),"Brown");
        Assert.assertEquals(booking.getTotalprice(),111);
        Assert.assertEquals(booking.getDepositpaid(),true);
        Assert.assertEquals(booking.getAdditionalneeds(),"Breakfast");

        SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd");
        String checkin = DateFormat.format(booking.getBookingdates().getCheckin());
        String checkout = DateFormat.format(booking.getBookingdates().getCheckout());
        Assert.assertEquals("2018-01-01",checkin );
        Assert.assertEquals("2019-01-01",checkout );

    }

    @Test
    public void gettingAllIdTest(){
        Specification.installSpecification(Specification.requestSpec(URL),Specification.responseSpecGood());
        List<Integer> bookingList = given()
                .when()
                .get("booking")
                .then().log().all()
                .extract().body().jsonPath().getList("bookingid",Integer.class);

        Assert.assertFalse(bookingList.isEmpty());
        Assert.assertTrue(bookingList.size()>10);
        Assert.assertNotNull(bookingList.get(0));
        Assert.assertNotNull(bookingList.get(bookingList.size()-1));

    }

    @Test
    public void updateRandomTotalpriceTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecGood());
        List<Integer> bookingList = given()
                .when()
                .get("booking")
                .then()
                .extract().body().jsonPath().getList("bookingid", Integer.class);

        Random rand = new Random();
        String URLOfUpdatedBooking = "booking/"+ bookingList.get(rand.nextInt(bookingList.size()));

        AuthorizationUser admin = new AuthorizationUser("admin", "password123");
        SuccessfulAuthorization authorization = given()
                .header("Accept", "application/json")
                .body(admin)
                .when()
                .post("auth")
                .then().log().all()
                .extract().as(SuccessfulAuthorization.class);

        int totalPriceNew = 12;
        String jsonString  = "{\"totalprice\":"+totalPriceNew+"}";

        Booking updatedBooking = given()
                .header("Accept", "application/json")
                .cookie("token",authorization.getToken())
                .body(jsonString)
                .when()
                .patch(URLOfUpdatedBooking)
                .then().log().all()
                .extract().as(Booking.class);

        Assert.assertEquals(updatedBooking.getTotalprice(),totalPriceNew);
    }

    @Test
    public void updateRandomAllDataTest() {
        Specification.installSpecification(Specification.requestSpec(URL), Specification.responseSpecGood());
        List<Integer> bookingList = given()
                .when()
                .get("booking")
                .then()
                .extract().body().jsonPath().getList("bookingid", Integer.class);

        Random rand = new Random();
        String URLOfUpdatedBooking = "booking/"+ bookingList.get(rand.nextInt(bookingList.size()));

        AuthorizationUser admin = new AuthorizationUser("admin", "password123");
        SuccessfulAuthorization authorization = given()
                .header("Accept", "application/json")
                .body(admin)
                .when()
                .post("auth")
                .then().log().all()
                .extract().as(SuccessfulAuthorization.class);


        Booking oldDataBooking = given()
                .header("Accept", "application/json")
                .when()
                .get(URLOfUpdatedBooking)
                .then()
                .extract().as(Booking.class);

        String newFirstName = oldDataBooking.getFirstname()+"Updated";
        String newLastName = oldDataBooking.getLastname()+"Updated";
        String newAdditionalneeds = oldDataBooking.getAdditionalneeds()+"Updated";

        String jsonString  = "{\n" +
                "    \"firstname\" : \""+newFirstName+"\",\n" +
                "    \"lastname\" : \""+newLastName+"\",\n" +
                "    \"totalprice\" : 111,\n" +
                "    \"depositpaid\" : true,\n" +
                "    \"bookingdates\" : {\n" +
                "        \"checkin\" : \"2018-01-01\",\n" +
                "        \"checkout\" : \"2019-01-01\"\n" +
                "    },\n" +
                "    \"additionalneeds\" : \""+newAdditionalneeds+"\"\n" +
                "}";

        Booking updatedBooking = given()
                .header("Accept", "application/json")
                .cookie("token",authorization.getToken())
                .body(jsonString)
                .when()
                .put(URLOfUpdatedBooking)
                .then().log().all()
                .extract().as(Booking.class);

        Assert.assertEquals(updatedBooking.getFirstname(),newFirstName);
        Assert.assertEquals(updatedBooking.getLastname(),newLastName);
        Assert.assertEquals(updatedBooking.getAdditionalneeds(),newAdditionalneeds);
    }

    @Test
    public void deleteRandomBookingTest() {

        List<Integer> bookingList = given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .when()
                .get("https://restful-booker.herokuapp.com/booking")
                .then()
                .assertThat()
                .statusCode(200)
                .extract().body().jsonPath().getList("bookingid", Integer.class);

        Random rand = new Random();
        int randomId = bookingList.get(rand.nextInt(bookingList.size()));
        String URLOfUpdatedBooking = "https://restful-booker.herokuapp.com/booking/" + randomId;


        AuthorizationUser admin = new AuthorizationUser("admin", "password123");
        SuccessfulAuthorization authorization = given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .body(admin)
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
                .then().log().all()
                .assertThat()
                .statusCode(200)
                .extract().as(SuccessfulAuthorization.class);

        given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .cookie("token",authorization.getToken())
                .when()
                .delete(URLOfUpdatedBooking)
                .then().log().all()
                .assertThat()
                .statusCode(201)
                .extract().body().toString().equals("Created");

        given()
                .header("Accept", "application/json")
                .contentType(ContentType.JSON)
                .when()
                .get(URLOfUpdatedBooking)
                .then().log().all()
                .assertThat()
                .statusCode(404);
    }
}
