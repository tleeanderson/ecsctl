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

(defn keyword-from-args [args]
  (keyword (clojure.string/join (cons (first args)
                                      (mapv clojure.string/capitalize
                                            (rest args))))))

(defn valid-keyword
  ([cli-args]
   (valid-keyword cli-args ecs-ops))
  ([[_ & args] ops]
   (let [kw (keyword-from-args args)]
     (when (contains? ops kw)
       kw))))

(defn keyword-from-long-arg [arg-name]
  (let [arg (subs arg-name 2)
        arg-words (clojure.string/split arg #"-")]
    (when (and (re-matches #"[a-z]" (str (first arg)))
               (re-matches #"[a-z-]+" arg))
      (keyword-from-args arg-words))))

;TODO need to account for cmd with no args
(defn validate-parts [{base :base cmd :cmd args :args} ops]
  (let [cmd-kw (keyword-from-args cmd)
        arg-kw (keyword-from-long-arg args)]
    (cond
      (not= base "ecsctl") (do
                             (println "base is not ecsctl")
                             (System/exit 1))
      (not (contains? ops cmd-kw)) (do
                                     (println cmd-kw "is not a valid ecs command")
                                     (System/exit 1))
      (or (nil? ((ops cmd-kw) :required))
        (nil? ((set ((ops cmd-kw) :required)) arg-kw))) (do
                                                       (println cmd-kw arg-kw "is not a valid ecs command")
                                                       (System/exit 1))
      :else (do
              (println cmd-kw arg-kw "is valid")))))
;
;(defn validate-cmd-args [{args :args}]
;  ;"--arg-1   value1   --arg-2 value2"
;  (let [cmd-args (filterv #(not (str/blank? %))
;                         (str/split (apply str args) #" "))]
;    (if (even? (count cmd-args))
;
;      nil))
;  )


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
