(ns darongmean.approvals-clj.nodejs
  (:require
   ["approvals/lib/Approvals" :as Approvals]
   ["approvals/lib/ManualNamer" :as lib-ManualNamer]
   ["approvals/lib/AUtils" :as AUtils]
   ["approvals/lib/StringWriter" :as lib-StringWriter]
   ["approvals/lib/Scrubbers" :as ScrubbersIndex]
   ["approvals/lib/Scrubbers/Scrubbers" :as Scrubbers]
   ["approvals/lib/Scrubbers/DateScrubber" :as DateScrubber]
   ["approvals/lib/Reporting/GenericDiffReporterBase" :as GenericDiffReporterBase]
   ["fs" :as fs]
   ["os" :as os]
   ["path" :as node-path]))

;;; os

(defn fs-existsSync [path]
  (.existsSync fs path))


(defn fs-readFileSync-toString [path]
  (-> fs
      (.readFileSync path)
      (.toString)))


(defn path-parse [path]
  (-> (.parse node-path path)
      (js->clj {:keywordize-keys true})))


(defn path-sep []
  (.-sep node-path))

;;; reporters

(defn GenericDiffReporter [{:keys [name exePath cmdArgs]}]
  ;; fixme hack to simulate "super" call
  (let [parent-obj (GenericDiffReporterBase. name)
        _          (aset parent-obj "exePath" (.searchForExecutable AUtils exePath))]
    {:name        (.-name parent-obj)
     :canReportOn (fn [file-name]
                    (.canReportOn parent-obj file-name))
     :report      (fn [approved received options]
                    (aset options "cmdArgs" (clj->js (concat cmdArgs [received approved])))
                    (.report parent-obj approved received options))}))

;;; scrubbers

(defn Scrubbers-createGuidScrubber []
  (.createGuidScrubber Scrubbers/Scrubbers))


(defn Scrubbers-createReqexScrubber [regex replacement]
  (.createReqexScrubber Scrubbers/Scrubbers regex replacement))


(defn DateScrubber-create [regex]
  (.create DateScrubber/DateScrubber regex))


(defn DateScrubber-getScrubberFor [example]
  (.getScrubberFor DateScrubber/DateScrubber example))

;;; lib

(defn ManualNamer [dirName testFileName]
  (lib-ManualNamer. dirName testFileName))


(defn getReceivedFile [^lib-ManualNamer namer ext]
  (.getReceivedFile namer ext))


(defn getApprovedFile [^lib-ManualNamer namer ext]
  (.getApprovedFile namer ext))


(defn StringWriter [config outputText ext]
  (lib-StringWriter. config outputText ext))


(defn getConfig [overrideOptions]
  (.getConfig Approvals (clj->js overrideOptions)))


(defn verifyWithControl [namer writer reporterFactory optionsOverride]
  (.verifyWithControl Approvals namer writer reporterFactory optionsOverride))

;;; init

(defonce ^:private __init__
  (when-not Approvals
    (throw (ex-info "https://github.com/approvals/Approvals.NodeJS not found" {}))))
