def root = new XmlParser().parse(inputFile)

def subsystems = root.profile.subsystem
def s = subsystems.find{it.name().getNamespaceURI().contains('urn:jboss:domain:ee:')}
def globalModules = s.appendNode('global-modules')
globalModules.appendNode('module', ['name':'org.jboss.resteasy.resteasy-legacy','services':'true'])

/**
 * Save the configuration to a new file
 */

def writer = new StringWriter()
writer.println('<?xml version="1.0" encoding="UTF-8"?>')
new XmlNodePrinter(new PrintWriter(writer)).print(root)
def f = new File(outputFile)
f.write(writer.toString())
