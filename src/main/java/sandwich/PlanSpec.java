package sandwich;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.condition.AnyTaskCondition;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.util.MapBuilder;

/**
 * Plan configuration for Bamboo.
 *
 * @see <a href="https://confluence.atlassian.com/display/BAMBOO/Bamboo+Specs">Bamboo Specs</a>
 */
@BambooSpec
public class PlanSpec {

    /**
     * Run 'main' to publish your plan.
     */
    public static void main(String[] args) throws Exception {
        // by default credentials are read from the '.credentials' file
        BambooServer bambooServer = new BambooServer("http://127.0.0.1:8085");

        Plan plan = new PlanSpec().createPlan();
        bambooServer.publish(plan);

        PlanPermissions planPermission = new PlanSpec().createPlanPermission(plan.getIdentifier());
        bambooServer.publish(planPermission);
    }

    PlanPermissions createPlanPermission(PlanIdentifier planIdentifier) {
        Permissions permissions = new Permissions()
                .userPermissions("admin", PermissionType.ADMIN)
                .groupPermissions("admin", PermissionType.ADMIN)
                .loggedInUserPermissions(PermissionType.BUILD)
                .anonymousUserPermissionView();

        return new PlanPermissions(planIdentifier)
                .permissions(permissions);
    }

    Project project() {
        return new Project()
                .name("Sandwich")
                .key("SAND");
    }

    Plan createPlan() {
        return new Plan(
                project(), "Avo Sandwich", "AVOS")
                .description("Testing Bamboo Java Specs")
                .stages(new Stage("Stage 1")
                        .jobs(new Job("Build and run", "RUN")
                                .tasks(new ScriptTask().inlineBody("echo Test")
                                        .conditions(new AnyTaskCondition(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-conditional-tasks:variableCondition"))
                                                .configuration(new MapBuilder()
                                                        .put("variable", "planRepository.branch")
                                                        .put("operation", "equals")
                                                        .put("value", "master")
                                                        .build()))
                                        .description("Conditional Task executes if planRepository.branch is master")
                                )
                        )
                );
    }
}
