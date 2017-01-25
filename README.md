# My Code Snippets

##spring/prototypeBeanFactoryExample

Spring OSGI registry doesn't support scope prototype beans.  Typical solution is to use a factory and the consuming beans call factory.createInstance() or something similar.  This also means that the consuming beans have to be injected with the factory and know to call createInstance().

Example creates a factory on in OSGI service but further wraps that factory with another factory extending from *org.springframework.beans.factory.FactoryBean*.   Spring will automatically inject the resulting beans as such the consuming beans need not know that a factory is involved.

##java/rpminputstream

Based on the RPM specs at this location.   Wraps RPM processing around InputStream interface.

http://refspecs.linuxbase.org/LSB_3.1.1/LSB-Core-generic/LSB-Core-generic/pkgformat.html

Responsible for processing RPM files to extract Lead, Signature, and Head sections.   Positions the inputstream to the beginning of payload.

Example of reading a file from the RPM
```
File file = new File("testrpm.rpm");
CpioArchiveInputStream cpioInputStream = RPMInputStream.getCpioArchiveInputStream(file);

CpioArchiveEntry cpioEntry;
while ((cpioEntry = cpioInputStream.getNextCPIOEntry()) != null) {
		if (cpioEntry.getName().contains("project.properties")) {
	                break;
		}
	}

Properties props = new Properties();
props.load(cpioInputStream);
```
