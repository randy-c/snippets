# My Code Snippets

##spring/prototypeBeanFactoryExample

Spring OSGI registry doesn't support scope prototype beans.  Typical solution is to use a factory and the consuming beans call factory.createInstance() or something similar.  This also means that the consuming beans have to be injected with the factory and know to call createInstance().

Example creates a factory on in OSGI service but further wraps that factory with another factory extending from *org.springframework.beans.factory.FactoryBean*.   Spring will automatically inject the resulting beans as such the consuming beans need not know that a factory is involved.

