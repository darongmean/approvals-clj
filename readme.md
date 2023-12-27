# approvals-clj

Approvals Testing Library for Clojure/Clojurescript.

It's an assertion library with a twist. 
Instead of comparing Clojure data and print the diff on the terminal, it writes data to file and launch your favorite diff tool.

### Why would I want to do that? 
My expectation after using this testing method:
- write less assertion codes
- diff tool would help me to spot issues faster than the terminal 
For more reasons, see https://approvaltests.com.


### ClojureScript

This is a wrapper of https://github.com/approvals/Approvals.NodeJS

### Clojure: TBD

# References

- https://approvaltests.com
- https://jestjs.io/docs/snapshot-testing
- https://github.com/DomainDrivenArchitecture/data-test
- https://www.texttest.org/

# License

Copyright © 2023 Darong Mean

Licensed under the term of the Mozilla Public License 2.0, see LICENSE.
