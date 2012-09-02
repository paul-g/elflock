LOGGER=org.apache.tools.ant.listener.AnsiColorLogger

all:
	ant -logger $(LOGGER) all
	
compile:
	ant -logger $(LOGGER) compile

clean:
	ant clean
	rm -f *.log
