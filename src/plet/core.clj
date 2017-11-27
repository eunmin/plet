(ns plet.core
  (:require [clojure.core.async :refer [chan]]))

(defn sleep [sec]
  (println "Sleep" (.getName (Thread/currentThread)))
  (Thread/sleep (* sec 1000)))

(defn plet-test []
  (let [a (future (sleep 1) 1)
        b (future (sleep 1) 2)
        c (future (let [r (+ @a @b)] (sleep 1) r))
        d (future (sleep 1) 2)
        e (future (sleep 1) 3)
        f (future (let [r (* @d @e)] (sleep 1) r))]
    (* @c @f @a @d)))

(defmacro plet [bindings & body]
  (let [bents (partition 2 bindings)
        _ (println bents)
        bindings (reduce (fn [r [b v]]
                           (conj r b `(do ~v)))
                         []
                         bents)]
    `(let ~bindings
       ~@body)))

(plet [a 1
       b 2
       c (+ a b)]
      (inc c))
