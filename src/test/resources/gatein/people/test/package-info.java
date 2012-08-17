@Application(defaultController = TestController.class)
@Bindings(
   @Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation= people.binding.GateInMetaProvider.class)
)
package gatein.people.test;
import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;