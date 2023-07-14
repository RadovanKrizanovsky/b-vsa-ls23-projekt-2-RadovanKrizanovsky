package sk.stuba.fei.uim.vsa.pr2;

import static org.reflections.scanners.Scanners.TypesAnnotated;

import java.util.HashSet;
import java.util.Set;
import javax.ws.rs.ApplicationPath;
import javax.ws.rs.Path;
import javax.ws.rs.core.Application;
import javax.ws.rs.ext.Provider;
import org.reflections.Reflections;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("api")
public class JAXRSApplicationConfiguration extends Application {

  public static final Logger log = LoggerFactory.getLogger(
    JAXRSApplicationConfiguration.class
  );

  private Set<Class<?>> classes;

  public JAXRSApplicationConfiguration() {
    classes = new HashSet<>();
    Reflections reflections = new Reflections("sk.stuba.fei.uim.vsa.pr2");
    classes = reflections.get(TypesAnnotated.with(Path.class).asClass());
    log.info("Registered resource classes: " + classes.toString());
    Set<Class<?>> providers = reflections.get(
      TypesAnnotated.with(Provider.class).asClass()
    );
    log.info("Registered feature providers: " + providers.toString());
    classes.addAll(providers);
  }

  @Override
  public Set<Class<?>> getClasses() {
    return classes;
  }
}
