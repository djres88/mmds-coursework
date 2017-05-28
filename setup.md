#Installing Scala + Spark
1. Prerequisites: Homebrew + Xcode.
Run the following commands in your terminal(1).
First, get Homebrew(2): 
```
/usr/bin/ruby -e "$(curl -fsSL https://raw.githubusercontent.com/Homebrew/install/master/install)"
```
You'll also need xcode-select(3):
```
xcode-select --install
```
Note: if you get a prompt requesting that you also install xcode, click "install."

2. Get Java.(4)
Run the following command in your terminal:
```
brew cask install java
```
You may be prompted to enter your password. Do that.

3. Install scala(5) and apache-spark(6).
Run the following command in your terminal:
```
brew install scala apache-spark
```

4. Alias spark-shell command to include context:
Add the following to your ./bashrc (or ./zshrc)
```
alias spark-shell='/usr/local/bin/spark-shell'
```

You're all set! Now you can run `spark-shell` to launch apache-spark.

Footnotes
1. I recommend iTerm. Here's a great config: link
2. Homebrew is the eas(y/iest) way to install applications and languages on OSX. This won't be the last time you use it. Promise.
3. xcode-select lets you access a bunch of necessary command line tools.
4. Why? Because Scala runs on Java. Do you wanna ask questions or get set up fast?
5. The scala repl as a standalone is useful for playing around/learning the language, but the hope is that (eventually) we'll be spending most of our time in the spark shell.
6. Installing apache-spark enables the command `spark-shell` (see above), which launches a shell for running spark on your machine. It is a very well-named command.