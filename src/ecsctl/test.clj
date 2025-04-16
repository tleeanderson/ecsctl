(ns ecsctl.test
  (:require [cognitect.aws.client.test-double :as test]
           [cognitect.aws.client.api :as aws]))

(def ecs (aws/client {:api :ecs}))

(defn test-client []
  (let [tc (test/client {:api :ecs :ops {:ListTasks {:response ["arn"]}}})]
    (aws/invoke tc {:op :ListTasks :request {:family "hey"}})))