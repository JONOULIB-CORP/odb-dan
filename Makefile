compile:
	bash comp.sh app.Server

web:
	bash run.sh app.Server web 2001

inter:
	bash run.sh app.Server inter 2002 localhost 2001

lb:
	bash run.sh app.Server lb 2003 localhost 2002

compile-servlet:
	bash comp.sh app.Serv