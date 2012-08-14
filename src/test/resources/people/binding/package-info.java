@Application(defaultController = A.class)
@Bindings(
   @Binding(value = org.exoplatform.services.organization.OrganizationService.class, implementation=GateInMetaProvider.class)
)
package people.binding;
import juzu.Application;
import juzu.plugin.binding.Binding;
import juzu.plugin.binding.Bindings;