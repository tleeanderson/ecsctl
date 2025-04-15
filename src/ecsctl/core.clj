(ns ecsctl.core
  ;;(:require [arc-solver2.image-utils :as iu])
  (:require [clojure.set :as cset])
  (:require [cognitect.aws.client.api :as aws]))

(def s3 (aws/client {:api :s3}))

(defn test-func []
  (aws/ops aws/client))

;;ecsctl list tasks
;;ecsctl tasks list

(def verb-to-noun {:list #{"tasks"}})

(defn valid-command? [verb noun]
  )

(defn va-test [args verbs nouns]
  (cond
    (not= (first args) "ecsctl") false
    (nil? (verbs (second args))) false
    (nil? (nouns (nth args 2))) false
    :else true))

(defn -main
  [x & args]
  (println (map #(type %) (flatten [x args])))
  (println "type of args: " (type args) " len args: " (count args)))
