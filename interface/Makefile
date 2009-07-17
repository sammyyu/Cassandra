INTERFACES := py java

all: $(INTERFACES:%=gen-%)

gen-%: cassandra.thrift
	thrift -gen $* $^