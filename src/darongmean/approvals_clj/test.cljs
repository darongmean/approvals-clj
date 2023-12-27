(ns darongmean.approvals-clj.test
  (:require-macros [darongmean.approvals-clj.test])
  (:require
   [clojure.set :as set]
   [clojure.string :as string]
   [clojure.tools.reader.edn :as edn]
   [darongmean.approvals-clj.cljs-test :as cljs-test-util]
   [darongmean.approvals-clj.nodejs :as nodejs]
   [zprint.core :as zprint]))

;;; reporters

(defn custom-reporter [{:keys [cmd args]}]
  (nodejs/GenericDiffReporter {:name cmd :exePath cmd :cmdArgs args}))


(defn diff-reporter [obj]
  (cond
    (string? obj) obj
    (:name obj) obj
    (map? obj) (custom-reporter obj)
    :default (throw (ex-info "unsupported reporter" {:reporter obj}))))

;;; configuration

(def ^:dynamic *current-opts* {})


(def default-opts
  "See
   - https://github.com/kkinnear/zprint/blob/main/doc/reference.md#map
   - https://github.com/approvals/Approvals.NodeJS/blob/d97be511053192133354d7b736c38171b42ca077/lib/config.js#L11
  "
  {:output-dir "test"
   :printer    {:style :justified
                :map   {:comma? false}}
   :reporters  ["gitdiff"]
   :approval   {:normalizeLineEndingsTo true
                :appendEOL              true}})


(defn merge-opts [override-opts]
  (let [opts (merge default-opts *current-opts* override-opts)
        opts (update opts :reporters #(mapv diff-reporter %))]
    opts))


(defn configure [override-opts]
  (set! *current-opts* (merge-opts override-opts)))


(defn get-approvals-nodejs-config [{:keys [reporters approval] :as _opts}]
  (let [conf (assoc approval :reporters reporters)]
    (nodejs/getConfig conf)))

;;; verify

(defn print-edn [data printer scrub]
  (let [text (zprint/zprint-str data printer)]
    (cond
      (fn? scrub) (scrub text)
      (seq scrub) (reduce (fn [txt f] (f txt)) text scrub)
      :default text)))


(defn get-output-file [output-dir]
  (let [{:keys [dir test-ns]} (-> (cljs-test-util/get-current-test-file)
                                  (nodejs/path-parse)
                                  (set/rename-keys {:name :test-ns}))
        file-path    (str output-dir "/" dir)
        test-context (if (= "" (cljs-test-util/get-testing-contexts))
                       (cljs-test-util/get-current-test-name)
                       (str (cljs-test-util/get-current-test-name) "." (cljs-test-util/get-testing-contexts)))
        file-name    (if (= test-context "")
                       test-ns
                       (str test-ns (nodejs/path-sep) test-context))
        file-name    (string/replace file-name " " "_")]
    {:file-path file-path
     :file-name file-name}))


(defn verify! [text file-path file-name ext opts]
  (let [conf   (get-approvals-nodejs-config opts)
        namer  (nodejs/ManualNamer file-path file-name)
        writer (nodejs/StringWriter conf text ext)]
    (try
      (nodejs/verifyWithControl namer writer nil conf)
      {:pass true}
      (catch :default ex
        {:fail     (ex-message ex)
         :received (-> namer
                       (nodejs/getReceivedFile ext)
                       nodejs/fs-readFileSync-toString)
         :approved (-> namer
                       (nodejs/getApprovedFile ext)
                       nodejs/fs-readFileSync-toString)}))))


;; todo
;; - (verify-table ...)
;; - (verify-all-combinations [i [a b c]] (do i))
;; - (verify-best-covering-pairs [i [a b c]] (do i))
;; - (verify-as-json ...)

(defn verify-impl [data {:keys [scrub printer reporters approval] :as options}]
  (let [{:keys [output-dir printer] :as opts} (if (every? nil? [printer reporters approval])
                                                *current-opts*
                                                (-> options
                                                    (select-keys [:printer :reporters :approval])
                                                    (merge-opts)))
        {:keys [file-path file-name]} (get-output-file output-dir)
        text (print-edn data printer scrub)]
    (verify! text file-path file-name ".edn" opts)))

;;; init state

(defonce ^:private __init__
  (do
    (if (nodejs/fs-existsSync "approvals-clj.edn")
      (-> (nodejs/fs-readFileSync-toString "approvals-clj.edn")
          (edn/read-string)
          (configure))
      (configure {}))))
