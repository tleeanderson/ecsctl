(ns ecsctl.core
  ;;(:require [arc-solver2.image-utils :as iu])
  (:require [clojure.set :as cset])
  (:require [clojure.string])
  (:require [cognitect.aws.client.api :as aws]))

(def ecs-client (aws/client {:api :ecs}))
(def ecs-ops (aws/ops ecs-client))
(def prog-name "ecsctl")
(def usage (str prog-name " [verb] [noun]"))

;example inputs
(def gi ["ecsctl" "list" "tasks"])

(defn keyword-from-args [cli-args]
  (keyword (clojure.string/join (mapv clojure.string/capitalize cli-args))))

(defn valid-keyword
  ([cli-args]
   (valid-keyword cli-args ecs-ops))
  ([[_ & args] ops]
   (let [kw (keyword-from-args args)]
     (when (contains? ops kw)
       kw))))

(defn valid-command [cli-args]
  (let [args (valid-keyword cli-args)]
    (cond
      ;TODO might not need this check
      (not= (first cli-args) prog-name) (do
                                          (println "error: command must start with" prog-name)
                                          (println usage))
      (nil? args) (do
                    (if (empty? (rest cli-args))
                      (println "error: no arguments provided" )
                      (println "error:" (clojure.string/join " " (rest cli-args))
                               "are not valid arguments"))
                    (println usage))
      :else (do (println cli-args "is a valid command")
                args))))

(defn -main
  [fa & args]
  (valid-command (apply list fa args)))
