(ns darongmean.approvals-clj.cljs-test
  (:require
   [cljs.test :refer [get-current-env testing-contexts-str]]))


(defn get-current-test-file
  ([]
   (get-current-test-file {:var (first (:testing-vars (get-current-env)))}))
  ([m]
   (:file (meta (:var m)))))


(defn get-current-test-name
  ([]
   (get-current-test-name {:var (first (:testing-vars (get-current-env)))}))
  ([m]
   (str (:name (meta (:var m))))))


(defn get-testing-contexts []
  (testing-contexts-str))
