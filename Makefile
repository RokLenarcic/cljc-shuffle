test:
	yarn install
	npx shadow-cljs compile tests
	npx karma start --single-run
	clj -M:test:runner

.PHONY: test
