#A. Data Mining
*Data mining*: the discovery of "models" for data. Types of modeling:

##1. Statistical Modeling
To statisticians, data mining is the construction of a statistical model, i.e. an underlying (population) distribution from which the visible (sample) distribution is drawn.

Example: Suppose our data is a set of numbers. If we decide our data comes from a Gaussian distribution, we can construct a model of the distribution by finding the µ and σ. The µ and σ become our *model* of the data.

##2. Machine Learning Models
ML works well w/ for modeling data when we *don't* know what we're looking for. In the Netflix challenge, for example, it's unclear what drives a user's movie preferences. ML algorithms can construct a model of the data in cases where causal variables are unknown.

However, ML fares no better than direct algorithms when analyzing data where we understand the causal variables (e.g. WhizBang! "intelligently" searching for resume pages when we mostly already know the keywords).

##3. Computational Approaches to Modeling
*Compute the µ and σ of a set of numbers? Unclear from the example given how this differs from statistical modeling.*

##4. Summarization
- PageRank (see ch5)
- Clustering (see ch7)

##5. Feature Extraction
Typical feature-based model represents data via connections between its most extreme examples.

E.g. Bayes nets: find the strongest statistical dependencies between objects in a set, and use *only* those in representing all statistical connections. See *likeness*.

Other examples:
* *Frequent Itemsets* (ch6). E.g. market baskets. You often find cheese in a basket with macaroni.
* *Similar items (ch3)*. E.g. treating customers as the set of items they bought.

#B. Statistical Limits on Data Mining
It's pretty much impossible to predict extremely rare features from mundane data, because your model's "matches" are almost always random occurrences.

For example, looking for domestic terrorism in travel records and credit card bills.

TL;DR - *Calculate the expected number of occurrences of the events you are
looking for, on the assumption that data is random. If this number is significantly
larger than the number of real instances you hope to find, then you must
expect almost anything you find to be bogus, i.e., a statistical artifact rather
than evidence of what you are looking for.*(p5)

If you attempt to model activity A when *actual A much rarer than random activity that looks like A*, then nearly all your model's predictions will be false positives.

#C. Good to Know (refreshers)


5. *e*
The constant e = 2.7182818 · · · has a number of useful special properties. In
particular, e is the limit of (1 + 1
x)x as x goes to infinity.

More:
* `(1 + a)^b` ~ `e^ab`.
* `(1 − 1/x)^x` ~ `1/e`.

Some other useful approximations follow from the Taylor expansion of e^x.
* e^x = ∑i=0,inf x^i/i!, or `e^x = 1+ x + x^2/2 + x^3/6...`
  - When x is large, the above series converges slowly, although it does converge because n! grows faster than xn for any constant x.
  - However, when x is small, either positive or negative, the series converges rapidly, and only a few terms are necessary to get a good approximation.
