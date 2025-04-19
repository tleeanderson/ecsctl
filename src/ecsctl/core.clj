(ns ecsctl.core
  ;;(:require [arc-solver2.image-utils :as iu])
  (:require [clojure.set :as cset])
  (:require [clojure.string])
  (:require [cognitect.aws.client.api :as aws]))

(def ecs-client (aws/client {:api :ecs}))
(def ecs-ops (aws/ops ecs-client))
(def prog-name "ecsctl")
(def usage (str prog-name " [verb] [noun]"))

(defn keyword-from-args [cli-args]
  (keyword (clojure.string/join (mapv clojure.string/capitalize cli-args))))

(defn valid-args?
  ([cli-args]
   (valid-args? cli-args ecs-ops))
  ([cli-args ops]
   (contains? ops (keyword-from-args cli-args))))

(defn valid-command? [cli-args]
  (cond
    ;TODO might not need this check
    (not= (first cli-args) prog-name) (do
                                        (println "error: command must start with" prog-name)
                                        (println usage)
                                        false)
    (false? (valid-args? (rest cli-args))) (do
                                             (if (empty? (rest cli-args))
                                               (println "error: no arguments provided" )
                                               (println "error:" (clojure.string/join " " (rest cli-args))
                                                        "are not valid arguments"))
                                             (println usage)
                                             false)
    :else (do (println cli-args "is a valid command")
              true)))

(defn -main
  [fa & args]
  (valid-command? (apply list fa args)))
