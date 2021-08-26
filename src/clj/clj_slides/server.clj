(ns clj-slides.server
    (:require
     [clj-slides.handler :refer [app-creator]]
     [ring.adapter.jetty :refer [run-jetty]]
     [clojure.tools.cli :refer [parse-opts]])
    (:gen-class))

(def cli-options
  [["-i" "--input FILE" "Slides file"
    :default "blah.md"]
   ["-p" "--port PORT" "Port number"
    :default 3000
    :parse-fn #(Integer/parseInt %)
    :validate [#(< 0 % 0x10000) "Must be between 0 and 65536"]]])

(defn -main [& args]
  (let [parsed-opts (parse-opts args cli-options)
        port (get-in parsed-opts [:options :port])
        slides-path (get-in parsed-opts [:options :input])]
    (run-jetty (app-creator slides-path) {:port port :join? false})))
