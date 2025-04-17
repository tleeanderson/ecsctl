(ns ecsctl.test
  (:require [cognitect.aws.client.test-double :as test]
           [cognitect.aws.client.api :as aws]))

(def ecs-client (aws/client {:api :ecs}))

(def literal-test-client (test/client {:api :ecs :ops {:ListTasks
                                                       {:response ["literal-example-some-arn"]}}}))

(def func-test-client (test/client {:api :ecs :ops {:ListTasks (fn [{:keys [op request] :as op-map}]
                                                                 {:response ["func-example-some-arn"]})}}))

(defn list-tasks-test-call [test-client]
    (aws/invoke test-client {:op :ListTasks :request {:family "hey"}}))