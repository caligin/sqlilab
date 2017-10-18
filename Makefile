sources=$(wildcard src/**/*.clj)
tests=$(wildcard test/**/*_test.clj)

.PHONY: all clean run shell test

all: test build

build: target/sqlilab-0.1.0-SNAPSHOT.jar

target/sqlilab-0.1.0-SNAPSHOT.jar: $(sources) project.clj
	lein jar

test: $(sources) $(tests)
	lein test

run: build
	lein run

shell:
	lein repl

clean:
	rm -rf target
