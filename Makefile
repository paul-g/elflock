LOGGER:=org.apache.tools.ant.listener.AnsiColorLogger
ANT:=ant -logger $(LOGGER)

build: clean
	$(ANT) build

clean:
	$(ANT) clean
	rm -f *.log
