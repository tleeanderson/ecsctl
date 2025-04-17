(ns ecsctl.core
  ;;(:require [arc-solver2.image-utils :as iu])
  (:require [clojure.set :as cset])
  (:require [clojure.string])
  (:require [cognitect.aws.client.api :as aws]))

(def ecs-client (aws/client {:api :ecs}))

(def ecs-ops (aws/ops ecs-client))

(defn keyword-from-args [cli-args]
  (keyword (clojure.string/join (mapv clojure.string/capitalize cli-args))))

(defn valid-command?
  ([cli-args]
   (valid-command? cli-args ecs-ops))
  ([cli-args ops]
   (valid-command? cli-args ops (keyword-from-args cli-args)))
  ([cli-args ops user-op]
   (if (contains? ops user-op)
     (println cli-args "is a valid command")
     (println "error: " cli-args " is not a valid command"))))

(defn -main
  [fa & args]
  (if (= fa "ecsctl")
    (valid-command? args)
    (println "error: " fa " is not a valid command")))
