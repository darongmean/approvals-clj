# use fish instead of sh:
set shell := ["fish", "-c"]
# load .env file
set dotenv-load

_default:
    @just -l

deps:
    npm install

# shadow-cljs ...
shadow-cljs cmd args="":
    npx shadow-cljs {{cmd}} {{args}}

test:
    just shadow-cljs compile :env/test

# set env CLOJARS_USERNAME and CLOJARS_PASSWORD by .env
deploy-clojars:
    clj -T:build clean
    clj -T:build jar
    clj -T:build gen-pom
    clj -X:deploy
