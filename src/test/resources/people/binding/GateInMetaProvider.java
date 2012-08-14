package people.binding;

import javax.inject.Provider;

import org.exoplatform.container.RootContainer;

/** @author <a href="mailto:julien.viet@exoplatform.com">Julien Viet</a> */
public class GateInMetaProvider implements juzu.inject.ProviderFactory
{
   public <T> Provider<? extends T> getProvider(final Class<T> implementationType)
   {
      return new Provider<T>() {
         public T get() {
            T ret = (T)RootContainer.getInstance().getComponentInstanceOfType(implementationType);
            return ret;
         }
      };
   }
}
