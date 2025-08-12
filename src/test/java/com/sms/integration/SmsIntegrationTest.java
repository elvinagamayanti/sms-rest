package com.sms.integration;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.sms.controller.DeputiController;
import com.sms.controller.KegiatanController;
import com.sms.controller.OutputController;
import com.sms.controller.ProgramController;
import com.sms.controller.ProvinceController;
import com.sms.controller.RoleController;
import com.sms.service.ActivityLogService;
import com.sms.service.DeputiService;
import com.sms.service.DirektoratService;
import com.sms.service.KegiatanService;
import com.sms.service.NotificationService;
import com.sms.service.OutputService;
import com.sms.service.ProgramService;
import com.sms.service.ProvinceService;
import com.sms.service.RoleService;
import com.sms.service.SatkerService;
import com.sms.service.UserService;

/**
 * Standalone Integration Test untuk SMS System
 * 
 * Test ini tidak menggunakan Spring Boot context loading
 * untuk menghindari ApplicationContext failure
 */
@ExtendWith(MockitoExtension.class)
public class SmsIntegrationTest {

    private MockMvc mvc;

    // Mock all services
    @Mock
    private ProvinceService provinceService;
    @Mock
    private DeputiService deputiService;
    @Mock
    private RoleService roleService;
    @Mock
    private ProgramService programService;
    @Mock
    private OutputService outputService;
    @Mock
    private KegiatanService kegiatanService;
    @Mock
    private UserService userService;
    @Mock
    private ActivityLogService activityLogService;
    @Mock
    private NotificationService notificationService;
    @Mock
    private DirektoratService direktoratService;
    @Mock
    private SatkerService satkerService;

    @BeforeEach
    public void setUp() {
        // Setup MockMvc secara manual tanpa Spring context
        ProvinceController provinceController = new ProvinceController(provinceService);
        DeputiController deputiController = new DeputiController(deputiService);
        RoleController roleController = new RoleController(roleService);
        ProgramController programController = new ProgramController(programService);
        OutputController outputController = new OutputController(outputService);
        KegiatanController kegiatanController = new KegiatanController(kegiatanService, userService);

        mvc = MockMvcBuilders.standaloneSetup(
                provinceController,
                deputiController,
                roleController,
                programController,
                outputController,
                kegiatanController).build();
    }

    // ====================================
    // PROVINCE CONTROLLER TESTS
    // ====================================

    @Test
    public void testGetAllProvinces_ReturnsOk() throws Exception {
        mvc.perform(get("/api/provinces"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProvinceById_ReturnsOk() throws Exception {
        mvc.perform(get("/api/provinces/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProvinceByCode_ReturnsOk() throws Exception {
        mvc.perform(get("/api/provinces/code/31"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateProvince_ReturnsCreated() throws Exception {
        String provinceRequest = "{"
                + "\"name\": \"Test Province\","
                + "\"code\": \"TP01\""
                + "}";

        mvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(provinceRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateProvince_ReturnsOk() throws Exception {
        String updateRequest = "{"
                + "\"id\": 1,"
                + "\"name\": \"Updated Province\","
                + "\"code\": \"UP01\""
                + "}";

        mvc.perform(put("/api/provinces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testPatchProvince_ReturnsOk() throws Exception {
        String patchRequest = "{"
                + "\"name\": \"Patched Province\""
                + "}";

        mvc.perform(patch("/api/provinces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(patchRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProvince_ReturnsOk() throws Exception {
        mvc.perform(delete("/api/provinces/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetSatkersByProvinceCode_ReturnsOk() throws Exception {
        mvc.perform(get("/api/provinces/31/satkers"))
                .andExpect(status().isOk());
    }

    // ====================================
    // DEPUTI CONTROLLER TESTS
    // ====================================

    @Test
    public void testGetAllDeputis_ReturnsOk() throws Exception {
        mvc.perform(get("/api/deputis"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetDeputiById_ReturnsOk() throws Exception {
        mvc.perform(get("/api/deputis/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateDeputi_ReturnsCreated() throws Exception {
        String deputiRequest = "{"
                + "\"name\": \"Test Deputi\","
                + "\"code\": \"TD01\""
                + "}";

        mvc.perform(post("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON)
                .content(deputiRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateDeputi_ReturnsOk() throws Exception {
        String updateRequest = "{"
                + "\"id\": 1,"
                + "\"name\": \"Updated Deputi\","
                + "\"code\": \"UD01\""
                + "}";

        mvc.perform(put("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteDeputi_ReturnsOk() throws Exception {
        mvc.perform(delete("/api/deputis/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetUsersByDeputiId_ReturnsOk() throws Exception {
        mvc.perform(get("/api/deputis/1/users"))
                .andExpect(status().isOk());
    }

    // ====================================
    // ROLE CONTROLLER TESTS
    // ====================================

    @Test
    public void testGetAllRoles_ReturnsOk() throws Exception {
        mvc.perform(get("/api/roles"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetRoleById_ReturnsOk() throws Exception {
        mvc.perform(get("/api/roles/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateRole_ReturnsCreated() throws Exception {
        String roleRequest = "{"
                + "\"name\": \"TEST_ROLE\","
                + "\"displayName\": \"Test Role\","
                + "\"description\": \"Test role description\""
                + "}";

        mvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content(roleRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateRole_ReturnsOk() throws Exception {
        String updateRequest = "{"
                + "\"id\": 1,"
                + "\"name\": \"UPDATED_ROLE\","
                + "\"displayName\": \"Updated Role\""
                + "}";

        mvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteRole_ReturnsOk() throws Exception {
        mvc.perform(delete("/api/roles/1"))
                .andExpect(status().isOk());
    }

    // ====================================
    // PROGRAM CONTROLLER TESTS
    // ====================================

    @Test
    public void testGetAllPrograms_ReturnsOk() throws Exception {
        mvc.perform(get("/api/programs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetProgramById_ReturnsOk() throws Exception {
        mvc.perform(get("/api/programs/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateProgram_ReturnsCreated() throws Exception {
        String programRequest = "{"
                + "\"name\": \"Test Program\","
                + "\"code\": \"TP01\""
                + "}";

        mvc.perform(post("/api/programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(programRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateProgram_ReturnsOk() throws Exception {
        String updateRequest = "{"
                + "\"name\": \"Test Program\","
                + "\"code\": \"TP01\""
                + "}";

        mvc.perform(put("/api/programs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteProgram_ReturnsOk() throws Exception {
        mvc.perform(delete("/api/programs/1"))
                .andExpect(status().isOk());
    }

    // ====================================
    // OUTPUT CONTROLLER TESTS
    // ====================================

    @Test
    public void testGetAllOutputs_ReturnsOk() throws Exception {
        mvc.perform(get("/api/outputs"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetOutputById_ReturnsOk() throws Exception {
        mvc.perform(get("/api/outputs/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateOutput_ReturnsCreated() throws Exception {
        String outputRequest = "{"
                + "\"name\": \"Test Output\","
                + "\"code\": \"TO01\","
                + "\"programId\": 1"
                + "}";

        mvc.perform(post("/api/outputs")
                .contentType(MediaType.APPLICATION_JSON)
                .content(outputRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateOutput_ReturnsOk() throws Exception {
        String updateRequest = "{"
                + "\"name\": \"Updated Output\","
                + "\"code\": \"TO01\","
                + "\"programId\": 1"
                + "}";

        mvc.perform(put("/api/outputs/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteOutput_ReturnsOk() throws Exception {
        mvc.perform(delete("/api/outputs/1"))
                .andExpect(status().isOk());
    }

    // ====================================
    // KEGIATAN CONTROLLER TESTS
    // ====================================

    @Test
    public void testGetAllKegiatans_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanById_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testCreateKegiatan_ReturnsCreated() throws Exception {
        String kegiatanRequest = "{"
                + "\"name\": \"Test Kegiatan\","
                + "\"description\": \"Test kegiatan description\","
                + "\"outputId\": 1,"
                + "\"anggaran\": 1000000"
                + "}";

        mvc.perform(post("/api/kegiatans")
                .contentType(MediaType.APPLICATION_JSON)
                .content(kegiatanRequest))
                .andExpect(status().isCreated());
    }

    @Test
    public void testUpdateKegiatan_ReturnsOk() throws Exception {
        String updateRequest = "{"
                + "\"name\": \"Updated Kegiatan\","
                + "\"description\": \"Updated description\","
                + "\"outputId\": 1,"
                + "\"budget\": 2000000"
                + "}";

        mvc.perform(put("/api/kegiatans/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(updateRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testDeleteKegiatan_ReturnsOk() throws Exception {
        mvc.perform(delete("/api/kegiatans/1"))
                .andExpect(status().isOk());
    }

    // ====================================
    // KEGIATAN STATISTICS TESTS
    // ====================================

    @Test
    public void testGetKegiatanStatistics_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/statistics"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanStatisticsByDirektorat_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/statistics/direktorat"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanStatisticsByDeputi_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/statistics/deputi"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanBudgetByDirektorat_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/budget/direktorat"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanBudgetByDeputi_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/budget/deputi"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanWithoutDirektoratPJ_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/without-direktorat-pj"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanMonthlyStatistics_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/statistics/monthly")
                .param("year", "2024"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanByDirektorat_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/direktorat/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanByDeputi_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/deputi/1"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanByDeputiCode_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/deputi/code/D01"))
                .andExpect(status().isOk());
    }

    @Test
    public void testGetKegiatanByYearAndDirektorat_ReturnsOk() throws Exception {
        mvc.perform(get("/api/kegiatans/year/2024/direktorat/1"))
                .andExpect(status().isOk());
    }

    // ====================================
    // ERROR HANDLING TESTS
    // ====================================

    @Test
    public void testInvalidContentType_Returns415() throws Exception {
        mvc.perform(post("/api/provinces")
                .contentType(MediaType.TEXT_PLAIN)
                .content("Invalid content type"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    public void testMalformedJson_Returns400() throws Exception {
        String malformedJson = "{\"name\": \"Test\", \"code\": "; // Incomplete JSON

        mvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(malformedJson))
                .andExpect(status().isBadRequest());
    }

    @Test
    public void testEmptyRequestBody_Returns400() throws Exception {
        mvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(""))
                .andExpect(status().isBadRequest());
    }

    // ====================================
    // HTTP METHOD VALIDATION TESTS
    // ====================================

    @Test
    public void testInvalidHttpMethod_Returns405() throws Exception {
        mvc.perform(patch("/api/provinces")) // PATCH without ID
                .andExpect(status().isMethodNotAllowed());
    }

    // ====================================
    // ASSIGNMENT TESTS
    // ====================================

    @Test
    public void testKegiatanAssignToSatkers_ReturnsOk() throws Exception {
        String assignRequest = "{"
                + "\"satkerIds\": [1, 2, 3]"
                + "}";

        mvc.perform(post("/api/kegiatans/1/assign-satkers")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testKegiatanAssignToProvinces_ReturnsOk() throws Exception {
        String assignRequest = "{"
                + "\"provinceIds\": [1, 2]"
                + "}";

        mvc.perform(post("/api/kegiatans/1/assign-provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignRequest))
                .andExpect(status().isOk());
    }

    @Test
    public void testKegiatanAssignUser_ReturnsOk() throws Exception {
        String assignRequest = "{"
                + "\"userId\": 1"
                + "}";

        mvc.perform(post("/api/kegiatans/1/assign-user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(assignRequest))
                .andExpect(status().isOk());
    }

    // ====================================
    // COMPREHENSIVE ENDPOINT VALIDATION
    // ====================================

    @Test
    public void testAllMainGetEndpoints_ReturnOk() throws Exception {
        // Test all main GET endpoints
        mvc.perform(get("/api/provinces")).andExpect(status().isOk());
        mvc.perform(get("/api/deputis")).andExpect(status().isOk());
        mvc.perform(get("/api/roles")).andExpect(status().isOk());
        mvc.perform(get("/api/programs")).andExpect(status().isOk());
        mvc.perform(get("/api/outputs")).andExpect(status().isOk());
        mvc.perform(get("/api/kegiatans")).andExpect(status().isOk());
    }

    @Test
    public void testAllCreateEndpoints_ReturnCreated() throws Exception {
        // Test all main POST endpoints
        mvc.perform(post("/api/provinces")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"T01\"}"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/deputis")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"T01\" }"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/roles")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"TEST_ROLE\"}"))
                .andExpect(status().isCreated());

        mvc.perform(post("/api/programs")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Test\",\"code\":\"TP01\"}"))
                .andExpect(status().isCreated());
    }

    @Test
    public void testAllUpdateEndpoints_ReturnOk() throws Exception {
        // Test all main PUT endpoints
        mvc.perform(put("/api/provinces/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Updated\",\"code\":\"U01\"}"))
                .andExpect(status().isOk());

        mvc.perform(put("/api/deputis/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"Updated\",\"code\":\"U01\"}"))
                .andExpect(status().isOk());

        mvc.perform(put("/api/roles/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"id\":1,\"name\":\"UPDATED_ROLE\"}"))
                .andExpect(status().isOk());
    }

    @Test
    public void testAllDeleteEndpoints_ReturnOk() throws Exception {
        // Test all main DELETE endpoints
        mvc.perform(delete("/api/provinces/1")).andExpect(status().isOk());
        mvc.perform(delete("/api/deputis/1")).andExpect(status().isOk());
        mvc.perform(delete("/api/roles/1")).andExpect(status().isOk());
        mvc.perform(delete("/api/programs/1")).andExpect(status().isOk());
        mvc.perform(delete("/api/outputs/1")).andExpect(status().isOk());
        mvc.perform(delete("/api/kegiatans/1")).andExpect(status().isOk());
    }
}