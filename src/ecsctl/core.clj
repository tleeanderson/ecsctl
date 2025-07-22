(ns ecsctl.core
  (:gen-class)
  (:require [clojure.string])
  (:require [cognitect.aws.client.api :as aws]))

;examples
;(aws/invoke s3 {:op :ListBuckets})

(def ecs-client (aws/client {:api :ecs}))
(def ecs-ops (aws/ops ecs-client))
(def prog-name "ecsctl")
(def usage (str prog-name " [verb] [noun]"))

;example inputs
(def gi ["ecsctl" "list" "tasks"])

(defn cli-arg-parts [cli-cmd]
  (let [[cmd args] ((fn [[a & args] res]
                      (if (.startsWith a "--")
                        [res (cons a args)]
                        (recur args (conj res a)))) (rest cli-cmd) [])]
    {:base (first cli-cmd) :cmd cmd :args args}))

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

(defn handle-invoke [invoke-ret]
  (if (= (invoke-ret :cognitect.anomalies/category) :cognitect.anomalies/fault)
    (do
      (println "error message: " (invoke-ret :cognitect.anomalies/message) "\n"
               (invoke-ret :cognitect.aws.util/throwable)))
    (println invoke-ret)))

(defn run-cmd [args]
  (let [cmd (valid-command args)]
    (when (some? cmd)
      (handle-invoke (aws/invoke ecs-client {:op cmd})))))

(defn -main
  ;type ArraySeq
  [fa & args]
  (run-cmd (apply list fa args)))
