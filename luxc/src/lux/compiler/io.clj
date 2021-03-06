;;  Copyright (c) Eduardo Julian. All rights reserved.
;;  This Source Code Form is subject to the terms of the Mozilla Public License, v. 2.0.
;;  If a copy of the MPL was not distributed with this file,
;;  You can obtain one at http://mozilla.org/MPL/2.0/.

(ns lux.compiler.io
  (:require (lux [base :as & :refer [|case |let |do return* return fail fail*]])
            (lux.compiler [base :as &&])
            [lux.lib.loader :as &lib]))

;; [Utils]
(def ^:private !libs (atom nil))

;; [Resources]
(defn init-libs! []
  (reset! !libs (&lib/load)))

(defn read-file [source-dirs ^String file-name]
  (|case (&/|some (fn [^String source-dir]
                    (let [file (new java.io.File source-dir file-name)]
                      (if (.exists file)
                        (&/$Some file)
                        &/$None)))
                  source-dirs)
    (&/$Some file)
    (return (slurp file))

    (&/$None)
    (if-let [code (get @!libs file-name)]
      (return code)
      (&/fail-with-loc (str "[I/O Error] File doesn't exist: " file-name)))))
