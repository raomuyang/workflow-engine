import org.radrso.config.WorkflowEntity;
import org.radrso.plugins.JsonUtils;


/**
 * Created by raomengnan on 16-12-9.
 */
public class TestConfig {
    public static void main(String[] args) {
        TestConfig t = new TestConfig();
        String config = "/workflow1.json";
        System.out.println(t.getClass().getResource("/workflow1.json"));
        String fp = t.getClass().getResource(config).getPath();
        System.out.println(fp);

        WorkflowEntity workflowEntity = JsonUtils.loadJsonFile(fp, WorkflowEntity.class);
        System.out.println(workflowEntity);

    }
}
