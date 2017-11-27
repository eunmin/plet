(ns plet.core
  (:require [clojure.walk :refer [postwalk-replace]]
            [clojure.core.async :refer [go <!! <! timeout]]))

(defn remote-req [result]
  (Thread/sleep 1000)
  result)

(defmacro plet [bindings & body]
  (let [bents (partition 2 bindings)
        smap (into {} (map (fn [[b _]]
                             [b `(deref ~b)])
                           bents))
        bindings (vec (mapcat (fn [[b v]]
                                [b `(future ~(postwalk-replace smap v))])
                              bents))]
    `(let ~bindings
       ~@(postwalk-replace smap body))))

(time
 (let [a (remote-req 1)
       b (remote-req 1)
       c (+ a b)
       d (remote-req 1)
       e (remote-req 1)
       f (+ d e)]
   (+ c f)))

(time
 (plet [a (remote-req 1)
        b (remote-req 1)
        c (+ a b)
        d (remote-req 1)
        e (remote-req 1)
        f (+ d e)]
       (+ c f)))
