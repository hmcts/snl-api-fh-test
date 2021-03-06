package uk.gov.hmcts.futurehearings.snl.acceptance.resources;

import static uk.gov.hmcts.futurehearings.snl.acceptance.common.TestingUtils.generateStringForGivenLength;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createCompletePayloadHeader;
import static uk.gov.hmcts.futurehearings.snl.acceptance.common.helper.CommonHeaderHelper.createStandardPayloadHeader;

import org.junit.jupiter.params.provider.CsvFileSource;

import uk.gov.hmcts.futurehearings.snl.Application;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.RestClientTemplate;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.TestingUtils;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.delegate.CommonDelegate;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.delegate.dto.DelegateDTO;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.dto.SNLVerificationDTO;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.error.SNLCommonErrorVerifier;
import uk.gov.hmcts.futurehearings.snl.acceptance.common.verify.success.SNLCommonSuccessVerifier;

import java.text.MessageFormat;
import java.util.Random;
import java.util.UUID;

import io.restassured.response.Response;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.platform.suite.api.IncludeTags;
import org.junit.platform.suite.api.SelectClasses;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest(classes = {Application.class})
@ActiveProfiles("acceptance")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SelectClasses(POSTResourcesByUserHeaderValidationTest.class)
@IncludeTags("Put")
public class PUTResourcesByUserPayloadValidationTest extends ResourcesPayloadValidationTest {

    @Qualifier("CommonDelegate")
    @Autowired(required = true)
    private CommonDelegate commonDelegate;

    @Value("${resourcesByUserRootContext}")
    private String resourcesByUserRootContext;

    @Value("${resourcesByUser_idRootContext}")
    private String resourcesByUser_idRootContext;

    public String personIdHMCTS = null;

    @BeforeAll
    public void initialiseValues() throws Exception {
        super.initialiseValues();
        this.setHttpMethod(HttpMethod.PUT);
        this.setInputPayloadFileName("resources-by-user-mandatory-ui-based.json");
        personIdHMCTS = makePostResourcesByUserAndFetchUserId();
        this.setRelativeURL(String.format(resourcesByUser_idRootContext, personIdHMCTS));
        this.setHttpSuccessStatus(HttpStatus.NO_CONTENT);
        this.setRelativeURLForNotFound(this.getRelativeURL().replace("resources", "resource"));
        this.setSnlSuccessVerifier(new SNLCommonSuccessVerifier());
        this.setSnlErrorVerifier(new SNLCommonErrorVerifier());
    }

    @Test
    @DisplayName("Successfully validated response for a payload with all the mandatory required fields")
    public void test_successful_response_with_mandatory_elements_payload() throws Exception {
        this.setInputPayloadFileName("resources-by-username-complete.json");
        generatePayloadWithHMCTSID(personIdHMCTS, "/user/put/");
        DelegateDTO delegateDTO = buildDelegateDTO(getRelativeURL(),
                createStandardPayloadHeader(), getHttpMethod(), getHttpSuccessStatus());
        log.debug("The value of the Delegate Payload : " + delegateDTO.inputPayload());
        commonDelegate.test_expected_response_for_supplied_header(
                delegateDTO,
                getSnlSuccessVerifier(),
                new SNLVerificationDTO(getHttpSuccessStatus(), null, null, null));
    }

    @ParameterizedTest(name = "Update person role id and validated response")
    @DisplayName("Update person role id and validated response for a payload with all the mandatory required fields")
    @CsvFileSource(resources = "/uk/gov/hmcts/futurehearings/snl/acceptance/resources/input/template/user/put/valid_person_role_id.csv", numLinesToSkip = 1)
    public void test_update_positive_response_for_person_role_id_lov_ranges_with_mandatory_elements_payload(final String roleIDDesc, String roleID) throws Exception {
        this.setInputPayloadFileName("resources-by-username-optional-person-role-id.json");
        generatePayloadWithHMCTSID(String.valueOf(roleID), "/user/put/");
        DelegateDTO delegateDTO = buildDelegateDTO(getRelativeURL(),
                createStandardPayloadHeader(), getHttpMethod(), getHttpSuccessStatus());
        log.debug("The value of the Delegate Payload : " + delegateDTO.inputPayload());
        commonDelegate.test_expected_response_for_supplied_header(
                delegateDTO,
                getSnlSuccessVerifier(),
                new SNLVerificationDTO(getHttpSuccessStatus(), null, null, null));
    }

    //Getting the LOV Values from the MCGirr Spreadsheet - Hearing LOV's Locations Section
    //TODO - Clarify if the Source of the LOV's is Correct.
    @Test
    @DisplayName("Update PersonVenueID and validated response for a payload with all the mandatory required fields ")
    //Starting personVenueID range with 301 because we have created user with personVenueId with 300
    public void test_update_positive_response_for_person_venue_id_lov_ranges_with_mandatory_elements_payload() throws Exception {
        this.setInputPayloadFileName("resources-by-username-optional-person-venue-id.json");
        for (int personVenueID = 301; personVenueID < 386; personVenueID++) {
            generatePayloadWithHMCTSID(String.valueOf(personVenueID), "/user/put/");
            DelegateDTO delegateDTO = buildDelegateDTO(getRelativeURL(),
                    createStandardPayloadHeader(), getHttpMethod(), getHttpSuccessStatus());
            log.debug("The value of the Delegate Payload : " + delegateDTO.inputPayload());
            commonDelegate.test_expected_response_for_supplied_header(
                    delegateDTO,
                    getSnlSuccessVerifier(),
                    new SNLVerificationDTO(getHttpSuccessStatus(), null, null, null));
        }
    }

    @ParameterizedTest(name = "Update Positive Tests for Singular Fields : {0} - {1}")
    @CsvSource(value = {
            "personFirstName,x  HMI Test  Updated",
            "personLastName,x  HMI Test  Updated",
            "personRegistry,KNT",
            "personContactEmail,snlqa-updated@test.com",
            "personRoleId,130",
            "personVenueId,300",
            "personActiveDate,1999-10-02",
            "personInactiveDate,2000-12-19"
    }, nullValues = "NIL")
    public void test_positive_response_for_general_updated_payload(final String userTemplateKey,
                                                                   final String userTemplateValue) throws Exception {
        this.setInputPayloadFileName("resources-by-user-general-template.json");
        generatePayloadWithHMCTSIDAndField(userTemplateKey, userTemplateValue, "/user/put/");
        DelegateDTO delegateDTO = buildDelegateDTO(getRelativeURL(),
                createStandardPayloadHeader(), getHttpMethod(), getHttpSuccessStatus());
        log.debug("The value of the Delegate Payload : " + delegateDTO.inputPayload());
        commonDelegate.test_expected_response_for_supplied_header(
                delegateDTO,
                getSnlSuccessVerifier(),
                new SNLVerificationDTO(getHttpSuccessStatus(), null, null, null));
    }

    @ParameterizedTest(name = "Update Negative Tests for Singular Fields : {0} - {1}")
    @CsvSource(value = {
            "personFirstName,''", "personFirstName,' '", "personFirstName,C",
            "personLastName,''", "personLastName,' '", "personLastName,C",
            "personRegistry,''", "personRegistry,' '", "personRegistry,Z", "personRegistry,BR", "personRegistry,RGB", "personRegistry,C_FE",
            //"personContactEmail,''", "personContactEmail,' '", "personContactEmail,'xxxtest.com'", "personContactEmail,'x'", TODO - This should be a mandatory field
            "personActiveDate,''", "personActiveDate,' '", "personActiveDate,'13-11-1988'", "personActiveDate,'13-NOV-1988'", "personActiveDate,'1988-02-31'",
            "personSalutation,'Miss'" //Test to update a Non Existant Field
    }, nullValues = "NIL")
    public void test_negative_response_for_general_updated_payload(final String locationTemplateKey,
                                                                   String locationTemplateValue) throws Exception {
        SNLVerificationDTO snlVerificationDTO = null;
        switch (locationTemplateKey) {
            case "personFirstName":
                switch (locationTemplateValue) {
                    case "":
                    case " ":
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.userRequest.details.personFirstName: does not match the regex pattern ^[!-~]([ -~]*[!-~])?$]", null);
                        break;
                    default:
                        locationTemplateValue = generateStringForGivenLength(81, locationTemplateValue);//making the value to the Just beyond max length of the Field
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.userRequest.details.personFirstName: may only be 80 characters long]", null);
                        break;
                }
                break;
            case "personLastName":
                switch (locationTemplateValue) {
                    case "":
                    case " ":
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.userRequest.details.personLastName: does not match the regex pattern ^[!-~]([ -~]*[!-~])?$]", null);
                        break;
                    default:
                        locationTemplateValue = generateStringForGivenLength(81, locationTemplateValue);//making the value to the Just beyond max length of the Field
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.userRequest.details.personLastName: may only be 30 characters long]", null);
                        break;
                }
                break;
            case "personContactEmail":
                switch (locationTemplateValue) {
                    case "":
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.locationRequest.location.locationIdHMCTS: must be at least 1 characters long]", null);
                        break;
                    case " ":
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1001", "A Location resource with 'locationIdHMCTS' = ' ' already exists", null);
                        break;
                    default:
                        locationTemplateValue = generateStringForGivenLength(81, locationTemplateValue);//making the value to the Just beyond max length of the Field
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.locationRequest.location.locationIdHMCTS: may only be 8 characters long]", null);
                        break;
                }
                break;
            case "personRegistry":
                switch (locationTemplateValue) {
                    case "C_FE":
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004", "[$.userRequest.details." + locationTemplateKey + ": may only be 3 characters long]", null);
                        break;
                    default:
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1000", "'" + locationTemplateValue + "' is not a valid value for field '" + locationTemplateKey + "'", null);
                        break;
                }
                break;
            case "personActiveDate":
                switch (locationTemplateValue) {
                    default:
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004",
                                "[$.userRequest.details.personActiveDate: "+locationTemplateValue+" is an invalid date]",
                                null);
                        break;
                }
                break;
            case "personSalutation":
                switch (locationTemplateValue) {
                    default:
                        snlVerificationDTO = new SNLVerificationDTO(HttpStatus.BAD_REQUEST, "1004",
                                "[$.userRequest.details.personSalutation: is not defined in the schema and the schema does not allow additional properties]",
                                null);
                        break;
                }
                break;
            default:
                break;
        }
        this.setInputPayloadFileName("resources-by-user-general-template.json");
        generatePayloadWithHMCTSIDAndField(locationTemplateKey, locationTemplateValue, "/user/put/");
        DelegateDTO delegateDTO = buildDelegateDTO(getRelativeURL(),
                createStandardPayloadHeader(), getHttpMethod(), getHttpSuccessStatus());
        log.debug("The value of the Delegate Payload : " + delegateDTO.inputPayload());
        commonDelegate.test_expected_response_for_supplied_header(
                delegateDTO,
                getSnlErrorVerifier(),
                snlVerificationDTO);
    }


    private String makePostResourcesByUserAndFetchUserId() throws Exception {
        String randomString = UUID.randomUUID().toString();
        //randomString = randomString.substring(0,15).replace("-","_") + "@test.com";
        randomString = randomString + "@test.com";
        DelegateDTO delegateDTO = DelegateDTO.builder()
                .targetSubscriptionKey(getApiSubscriptionKey()).authorizationToken(getAuthorizationToken())
                .targetURL(resourcesByUserRootContext)
                .inputPayload(String.format(TestingUtils.readFileContents(String.format(INPUT_TEMPLATE_FILE_PATH, getInputFileDirectory()) +
                        "/user/post/" + getInputPayloadFileName()), randomString))
                .standardHeaderMap(createCompletePayloadHeader())
                .headers(null)
                .params(getUrlParams())
                .httpMethod(HttpMethod.POST)
                .status(HttpStatus.CREATED)
                .build();
        Response response = RestClientTemplate.shouldExecute(TestingUtils.convertHeaderMapToRestAssuredHeaders(delegateDTO.standardHeaderMap()),
                delegateDTO.authorizationToken(),
                delegateDTO.inputPayload(), delegateDTO.targetURL(),
                delegateDTO.params(), delegateDTO.status(), delegateDTO.httpMethod());
        log.debug("POST Response : " + response.getBody().asString());
        return randomString;
    }
}


