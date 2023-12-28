(ns build
  (:require
   [clojure.tools.build.api :as b]))

(def lib 'com.github.darongmean/approvals-clj)
(def version (format "0.0.%s" (b/git-count-revs nil)))
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"})
  (b/delete {:path "pom.xml"}))

(defn gen-pom [_]
  (b/write-pom {:target   "."
                :lib      lib
                :version  version
                :basis    basis
                :pom-data [[:licenses
                            [:license
                             [:name "Mozilla Public License 2.0"]
                             [:url "https://opensource.org/license/mpl-2-0/"]
                             [:distribution "https://github.com/darongmean/approvals-clj"]]]]
                :src-dirs ["src"]}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib       lib
                :version   version
                :basis     basis
                :pom-data  [[:licenses
                             [:license
                              [:name "Mozilla Public License 2.0"]
                              [:url "https://opensource.org/license/mpl-2-0/"]
                              [:distribution "https://github.com/darongmean/approvals-clj"]]]]
                :src-dirs  ["src"]})
  (b/copy-dir {:src-dirs   ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file  jar-file}))
