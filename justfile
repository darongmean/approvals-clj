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
    j shadow-cljs compile :env/test
