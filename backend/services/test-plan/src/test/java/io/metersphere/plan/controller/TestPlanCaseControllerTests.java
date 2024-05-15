package io.metersphere.plan.controller;

import io.metersphere.bug.domain.BugRelationCase;
import io.metersphere.bug.domain.BugRelationCaseExample;
import io.metersphere.bug.mapper.BugRelationCaseMapper;
import io.metersphere.dto.BugProviderDTO;
import io.metersphere.plan.domain.TestPlanFunctionalCase;
import io.metersphere.plan.domain.TestPlanFunctionalCaseExample;
import io.metersphere.plan.dto.request.*;
import io.metersphere.plan.mapper.TestPlanFunctionalCaseMapper;
import io.metersphere.provider.BaseAssociateBugProvider;
import io.metersphere.request.AssociateBugPageRequest;
import io.metersphere.request.BugPageProviderRequest;
import io.metersphere.sdk.util.JSON;
import io.metersphere.system.base.BaseTest;
import io.metersphere.system.controller.handler.ResultHolder;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.*;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.jdbc.SqlConfig;
import org.springframework.test.web.servlet.MvcResult;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@AutoConfigureMockMvc
public class TestPlanCaseControllerTests extends BaseTest {

    public static final String FUNCTIONAL_CASE_LIST_URL = "/test-plan/functional/case/page";
    public static final String FUNCTIONAL_CASE_TREE_URL = "/test-plan/functional/case/tree/";
    public static final String FUNCTIONAL_CASE_TREE_COUNT_URL = "/test-plan/functional/case/module/count";
    public static final String FUNCTIONAL_CASE_DISASSOCIATE_URL = "/test-plan/functional/case/disassociate";
    public static final String FUNCTIONAL_CASE_BATCH_DISASSOCIATE_URL = "/test-plan/functional/case/batch/disassociate";


    public static final String FUNCTIONAL_CASE_RUN_URL = "/test-plan/functional/case/run";
    public static final String FUNCTIONAL_CASE_BATCH_RUN_URL = "/test-plan/functional/case/batch/run";
    public static final String FUNCTIONAL_CASE_BATCH_UPDATE_EXECUTOR_URL = "/test-plan/functional/case/batch/update/executor";
    @Resource
    private TestPlanFunctionalCaseMapper testPlanFunctionalCaseMapper;
    @Resource
    BaseAssociateBugProvider baseAssociateBugProvider;
    @Resource
    BugRelationCaseMapper bugRelationCaseMapper;


    @Test
    @Order(1)
    @Sql(scripts = {"/dml/init_test_plan_case_relate_bug.sql"}, config = @SqlConfig(encoding = "utf-8", transactionMode = SqlConfig.TransactionMode.ISOLATED))
    public void testGetFunctionalCaseList() throws Exception {
        TestPlanCaseRequest request = new TestPlanCaseRequest();
        request.setProjectId(DEFAULT_PROJECT_ID);
        request.setCurrent(1);
        request.setPageSize(10);
        request.setTestPlanId("plan_1");
        this.requestPost(FUNCTIONAL_CASE_LIST_URL, request);
        request.setSort(new HashMap<>() {{
            put("createTime", "desc");
        }});
        MvcResult mvcResult = this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_LIST_URL, request);
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);
    }


    @Test
    @Order(2)
    public void testGetFunctionalCaseTree() throws Exception {
        MvcResult mvcResult = this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_TREE_URL + "plan_1");
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);

        this.requestGetWithOkAndReturn(FUNCTIONAL_CASE_TREE_URL + "plan_2");
    }

    @Test
    @Order(3)
    public void testGetFunctionalCaseTreeCount() throws Exception {
        TestPlanCaseRequest request = new TestPlanCaseRequest();
        request.setProjectId("123");
        request.setCurrent(1);
        request.setPageSize(10);
        request.setTestPlanId("plan_1");
        MvcResult mvcResult = this.requestPostWithOkAndReturn(FUNCTIONAL_CASE_TREE_COUNT_URL, request);
        String returnData = mvcResult.getResponse().getContentAsString(StandardCharsets.UTF_8);
        ResultHolder resultHolder = JSON.parseObject(returnData, ResultHolder.class);
        Assertions.assertNotNull(resultHolder);
    }


    @Test
    @Order(4)
    void disassociate() throws Exception {
        TestPlanDisassociationRequest request = new TestPlanDisassociationRequest();
        request.setTestPlanId("gyq_disassociate_plan_1");
        request.setId("gyq_disassociate_case_3");
        this.requestPostWithOk(FUNCTIONAL_CASE_DISASSOCIATE_URL, request);
    }

    @Test
    @Order(5)
    public void disassociateBatch() throws Exception {
        BasePlanCaseBatchRequest request = new BasePlanCaseBatchRequest();
        request.setTestPlanId("gyq_disassociate_plan_1");
        request.setSelectAll(true);
        request.setExcludeIds(List.of("gyq_disassociate_case_2"));
        this.requestPostWithOk(FUNCTIONAL_CASE_BATCH_DISASSOCIATE_URL, request);
        TestPlanFunctionalCaseExample testPlanFunctionalCaseExample = new TestPlanFunctionalCaseExample();
        testPlanFunctionalCaseExample.createCriteria().andTestPlanIdEqualTo("gyq_disassociate_plan_1");
        List<TestPlanFunctionalCase> testPlanFunctionalCases = testPlanFunctionalCaseMapper.selectByExample(testPlanFunctionalCaseExample);
        Assertions.assertEquals(1, testPlanFunctionalCases.size());
        request = new BasePlanCaseBatchRequest();
        request.setTestPlanId("gyq_disassociate_plan_1");
        request.setSelectAll(false);
        request.setSelectIds(List.of("gyq_disassociate_case_2"));
        this.requestPostWithOk(FUNCTIONAL_CASE_BATCH_DISASSOCIATE_URL, request);
        testPlanFunctionalCases = testPlanFunctionalCaseMapper.selectByExample(testPlanFunctionalCaseExample);
        Assertions.assertEquals(0, testPlanFunctionalCases.size());
    }

    @Test
    @Order(5)
    public void getAssociateBugList() throws Exception {
        BugPageProviderRequest request = new BugPageProviderRequest();
        request.setSourceId("test_plan_case_id");
        request.setProjectId(DEFAULT_PROJECT_ID);
        request.setCurrent(1);
        request.setPageSize(10);
        BugProviderDTO bugProviderDTO = new BugProviderDTO();
        bugProviderDTO.setName("第二个");
        List<BugProviderDTO> operations = new ArrayList<>();
        operations.add(bugProviderDTO);
        Mockito.when(baseAssociateBugProvider.getBugList("bug_relation_case", "test_plan_case_id", "bug_id", request)).thenReturn(operations);
        this.requestPostWithOkAndReturn("/test-plan/functional/case/associate/bug/page", request);
    }

    @Test
    @Order(9)
    public void testAssociateBugs() throws Exception {
        TestPlanCaseAssociateBugRequest request = new TestPlanCaseAssociateBugRequest();
        request.setCaseId("fc_1");
        request.setTestPlanCaseId("relate_case_1");
        request.setTestPlanId("plan_1");
        request.setProjectId(DEFAULT_PROJECT_ID);
        List<String> ids = new ArrayList<>();
        ids.add("bug_1");
        Mockito.when(baseAssociateBugProvider.getSelectBugs(request, false)).thenReturn(ids);
        this.requestPostWithOkAndReturn("/test-plan/functional/case/associate/bug", request);
        AssociateBugPageRequest associateBugPageRequest = new AssociateBugPageRequest();
        associateBugPageRequest.setProjectId(DEFAULT_PROJECT_ID);
        associateBugPageRequest.setCurrent(1);
        associateBugPageRequest.setPageSize(10);
        associateBugPageRequest.setTestPlanCaseId("relate_case_1");
        this.requestPostWithOkAndReturn("/test-plan/functional/case/has/associate/bug/page", associateBugPageRequest);


    }

    @Test
    @Order(10)
    public void testDisassociateBug() throws Exception {
        BugRelationCaseExample bugRelationCaseExample = new BugRelationCaseExample();
        bugRelationCaseExample.createCriteria().andTestPlanCaseIdEqualTo("relate_case_1").andTestPlanIdEqualTo("plan_1");
        List<BugRelationCase> bugRelationCases = bugRelationCaseMapper.selectByExample(bugRelationCaseExample);
        this.requestGetWithOk("/test-plan/functional/case/disassociate/bug/" + bugRelationCases.get(0).getId());
    }


    @Test
    @Order(11)
    public void testFunctionalCaseRun() throws Exception {
        TestPlanCaseRunRequest request = new TestPlanCaseRunRequest();
        request.setProjectId("1234");
        request.setId("relate_case_3");
        request.setTestPlanId("plan_2");
        request.setCaseId("fc_1");
        request.setLastExecResult("SUCCESS");
        request.setStepsExecResult("123");
        request.setContent("12334");
        request.setNotifier("123");
        this.requestPostWithOk(FUNCTIONAL_CASE_RUN_URL, request);
        request.setLastExecResult("ERROR");
        this.requestPostWithOk(FUNCTIONAL_CASE_RUN_URL, request);

    }



    @Test
    @Order(12)
    public void testFunctionalCaseBatchRun() throws Exception {
        TestPlanCaseBatchRunRequest request = new TestPlanCaseBatchRunRequest();
        request.setProjectId("1234");
        request.setTestPlanId("plan_2");
        request.setLastExecResult("SUCCESS");
        request.setContent("12334");
        request.setNotifier("123");
        request.setSelectAll(true);
        this.requestPostWithOk(FUNCTIONAL_CASE_BATCH_RUN_URL, request);
        request.setSelectAll(false);
        request.setSelectIds(List.of("relate_case_3"));
        request.setLastExecResult("ERROR");
        this.requestPostWithOk(FUNCTIONAL_CASE_BATCH_RUN_URL, request);

    }


    @Test
    @Order(13)
    public void testBatchUpdateExecutor() throws Exception {
        TestPlanCaseUpdateRequest request = new TestPlanCaseUpdateRequest();
        request.setUserId("test_user");
        request.setTestPlanId("plan_4");
        request.setSelectAll(true);
        this.requestPostWithOk(FUNCTIONAL_CASE_BATCH_UPDATE_EXECUTOR_URL, request);
        request.setTestPlanId("plan_2");
        request.setSelectAll(false);
        request.setSelectIds(List.of("relate_case_3"));
        this.requestPostWithOk(FUNCTIONAL_CASE_BATCH_UPDATE_EXECUTOR_URL, request);

    }
}