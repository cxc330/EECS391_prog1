javac -cp 'lib/SimpleRTSv3.1.jar' src/edu/cwru/SimpleRTS/agent/RCAgent2.java

java -cp lib/SimpleRTSv3.1.jar:src edu.cwru.SimpleRTS.Main --config data/proj1Config.xml data/rc_3m5t.xml --agent  edu.cwru.SimpleRTS.agent.RCAgent2 0 --agent edu.cwru.SimpleRTS.agent.visual.VisualAgent 0 --agentparam true --agentparam true
