(ns darongmean.approvals-clj.scrub
  (:refer-clojure :exclude [uuid])
  (:require
   [darongmean.approvals-clj.nodejs :as nodejs]))


(defn uuid []
  (nodejs/Scrubbers-createGuidScrubber))


(defn regex
  ([match]
   (regex match #(str "<scrubbed_" % ">")))
  ([match replacement]
   (nodejs/Scrubbers-createReqexScrubber match replacement)))


(defn date [match]
  (nodejs/DateScrubber-create match))


(defn date-match-example [example]
  (nodejs/DateScrubber-getScrubberFor example))
