(ns darongmean.approvals-clj.scrubber-test
  (:require
   [clojure.test :refer [deftest testing]]
   [darongmean.approvals-clj.scrub :as scrub]
   [darongmean.approvals-clj.test :as t]))


(deftest guid-scrubber-test
  (t/verify {:scrub (scrub/uuid)}
    {:uuid (random-uuid)}))


(deftest regex-scrubber-test
  (testing "regex"
    (t/verify {:scrub (scrub/regex #"ab")}
      {:text "ab"}))
  (testing "replacement string"
    (t/verify {:scrub (scrub/regex #"ab" "<replaced>")}
      {:text "ab"}))
  (testing "replacement function"
    (t/verify {:scrub (scrub/regex #"ab" #(str "<replaced_" % ">"))}
      {:text "ab"})))


(deftest date-match-example-scrubber-test
  (t/verify {:scrub (scrub/date-match-example "2024-01-01T00:00:00Z")}
    {:date "2023-12-25T11:21:26Z"}))


(deftest date-scrubber-test
  (t/verify {:scrub (scrub/date #"\d{8}T\d{6}Z")}
    {:date "20210505T091112Z"}))


(deftest multi-scrubber-test
  (t/verify {:scrub [(scrub/date #"\d{8}T\d{6}Z") (scrub/uuid)]}
    {:uuid (random-uuid) :date "20210505T091112Z"}))
