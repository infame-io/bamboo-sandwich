package sandwich;

import com.atlassian.bamboo.specs.api.BambooSpec;
import com.atlassian.bamboo.specs.api.builders.AtlassianModule;
import com.atlassian.bamboo.specs.api.builders.condition.AnyTaskCondition;
import com.atlassian.bamboo.specs.api.builders.credentials.SharedCredentialsIdentifier;
import com.atlassian.bamboo.specs.api.builders.credentials.SharedCredentialsScope;
import com.atlassian.bamboo.specs.api.builders.deployment.Deployment;
import com.atlassian.bamboo.specs.api.builders.deployment.Environment;
import com.atlassian.bamboo.specs.api.builders.deployment.ReleaseNaming;
import com.atlassian.bamboo.specs.api.builders.plan.Job;
import com.atlassian.bamboo.specs.api.builders.plan.Plan;
import com.atlassian.bamboo.specs.api.builders.plan.PlanIdentifier;
import com.atlassian.bamboo.specs.api.builders.plan.Stage;
import com.atlassian.bamboo.specs.api.builders.project.Project;
import com.atlassian.bamboo.specs.api.builders.repository.VcsChangeDetection;
import com.atlassian.bamboo.specs.api.builders.requirement.Requirement;
import com.atlassian.bamboo.specs.builders.repository.git.GitRepository;
import com.atlassian.bamboo.specs.builders.task.ScriptTask;
import com.atlassian.bamboo.specs.util.BambooServer;
import com.atlassian.bamboo.specs.api.builders.permission.Permissions;
import com.atlassian.bamboo.specs.api.builders.permission.PermissionType;
import com.atlassian.bamboo.specs.api.builders.permission.PlanPermissions;
import com.atlassian.bamboo.specs.util.MapBuilder;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulBuildPlanTrigger;
import com.atlassian.bamboo.specs.builders.trigger.AfterSuccessfulDeploymentTrigger;
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

        Deployment deployment = new PlanSpec().deployment();
        bambooServer.publish(deployment);
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
                                ).requirements(new Requirement("purpose")
                                        .matchValue("sandwich")
                                        .matchType(Requirement.MatchType.MATCHES)
                                )
                                .tasks(new ScriptTask().inlineBody("echo Test123")
                                        .conditions(new AnyTaskCondition(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-conditional-tasks:variableCondition"))
                                                .configuration(new MapBuilder()
                                                        .put("variable", "planRepository.branch")
                                                        .put("operation", "equals")
                                                        .put("value", "master")
                                                        .build()))
                                        .description("Conditional Task executes if planRepository.branch is master")
                                ).requirements(new Requirement("purpose")
                                        .matchValue("sandwich")
                                        .matchType(Requirement.MatchType.MATCHES)
                                )
                        )
                ).planRepositories(new GitRepository()
                        .name("k3s")
                        .url("git@github.com:infame-io/k3s.git")
                        .branch("master")
                        .authentication(new SharedCredentialsIdentifier("infame-io").scope(SharedCredentialsScope.GLOBAL))
                        .changeDetection(new VcsChangeDetection()));
    }

    Deployment deployment() {
        return new Deployment(new PlanIdentifier("SAND", "AVOS"), "Sandwich - AVOS")
                .releaseNaming(new ReleaseNaming("release-1")
                        .autoIncrement(true)
                )
                .environments(new Environment("NonProd")
                        .tasks(new ScriptTask().inlineBody("echo Test")
                                .conditions(new AnyTaskCondition(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-conditional-tasks:variableCondition"))
                                        .configuration(new MapBuilder()
                                                .put("variable", "planRepository.branch")
                                                .put("operation", "equals")
                                                .put("value", "master")
                                                .build()
                                        )
                                )
                                .description("Conditional Task executes if planRepository.branch is master")
                        )
                        .requirements(new Requirement("purpose")
                                .matchValue("sandwich")
                                .matchType(Requirement.MatchType.MATCHES)
                        )
                        .triggers(new AfterSuccessfulBuildPlanTrigger()
                                .description("Trigger NonProd Deployment"))
                )
                .environments(new Environment("NonProd Release")
                        .tasks(new ScriptTask().inlineBody("echo Test")
                                .conditions(new AnyTaskCondition(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-conditional-tasks:variableCondition"))
                                        .configuration(new MapBuilder()
                                                .put("variable", "planRepository.branch")
                                                .put("operation", "equals")
                                                .put("value", "master")
                                                .build()))
                                .description("Conditional Task executes if planRepository.branch is master")
                        )
                        .requirements(new Requirement("purpose")
                                .matchValue("sandwich")
                                .matchType(Requirement.MatchType.MATCHES)
                        )
                        .triggers(new AfterSuccessfulDeploymentTrigger("NonProd")
                                .description("Trigger NonProd Deployment"))
                )
                .environments(new Environment("Prod")
                        .tasks(new ScriptTask().inlineBody("echo Test")
                                .conditions(new AnyTaskCondition(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-conditional-tasks:variableCondition"))
                                        .configuration(new MapBuilder()
                                                .put("variable", "planRepository.branch")
                                                .put("operation", "equals")
                                                .put("value", "master")
                                                .build()))
                                .description("Conditional Task executes if planRepository.branch is master")
                        )
                        .requirements(new Requirement("purpose")
                                .matchValue("sandwich")
                                .matchType(Requirement.MatchType.MATCHES)
                        )
                )
                .environments(new Environment("Prod Release")
                        .tasks(new ScriptTask().inlineBody("echo Test")
                                .conditions(new AnyTaskCondition(new AtlassianModule("com.atlassian.bamboo.plugins.bamboo-conditional-tasks:variableCondition"))
                                        .configuration(new MapBuilder()
                                                .put("variable", "planRepository.branch")
                                                .put("operation", "equals")
                                                .put("value", "master")
                                                .build()))
                                .description("Conditional Task executes if planRepository.branch is master")
                        )
                        .requirements(new Requirement("purpose")
                                .matchValue("sandwich")
                                .matchType(Requirement.MatchType.MATCHES)
                        )
                        .triggers(new AfterSuccessfulDeploymentTrigger("Prod")
                                .description("Trigger NonProd Deployment"))
                );
    }
}
