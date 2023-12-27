(ns darongmean.approvals-clj.test)


(defmacro verify
  ([options-or-form & form]
   (let [{:keys [file line end-line column end-column]} (or (meta options-or-form) (meta &form))]
     (if (seq form)
       `(try
          (let [data#   (try
                          ~@form
                          (catch :default ex#
                            ex#))
                result# (verify-impl data# ~options-or-form)]
            (if (:pass result#)
              (clojure.test/report {:type     :pass :message nil
                                    :file     ~file :line ~line :end-line ~end-line :column ~column :end-column ~end-column
                                    :expected '~form :actual data#})
              (clojure.test/report {:type     :fail :message (:fail result#)
                                    :file     ~file :line ~line :end-line ~end-line :column ~column :end-column ~end-column
                                    :expected (:accepted result#) :actual (:received result#)})))
          (catch :default ex#
            (clojure.test/report {:type     :error :message nil
                                  :file     ~file :line ~line :end-line ~end-line :column ~column :end-column ~end-column
                                  :expected '~form :actual ex#})))
       `(try
          (let [data#   (try
                          ~options-or-form
                          (catch :default ex#
                            ex#))
                result# (verify-impl data# nil)]
            (if (:pass result#)
              (clojure.test/report {:type     :pass :message nil
                                    :file     ~file :line ~line :end-line ~end-line :column ~column :end-column ~end-column
                                    :expected '~options-or-form :actual data#})
              (clojure.test/report {:type     :fail :message (:fail result#)
                                    :file     ~file :line ~line :end-line ~end-line :column ~column :end-column ~end-column
                                    :expected (:accepted result#) :actual (:received result#)})))
          (catch :default ex#
            (clojure.test/report {:type     :error :message nil
                                  :file     ~file :line ~line :end-line ~end-line :column ~column :end-column ~end-column
                                  :expected '~options-or-form :actual ex#})))))))
