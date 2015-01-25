package framework;


public class Entity {
	private EventLogger logger;
	//HashMap/array/list of attributes/values
	//attributes could be speed, color, etc
	//
	//Also tweaks are sets of attribute changes
	//happy = speed++, chr+, agi-
	//
	//Entities also have elements. elements are visual parts of the entity that are affected by its attirbutes
	//
	//How the game works: game starts with one question:
	//-spawns an entity according to answer.
	//entity ask questions, modifying its attributes, until 

	double velocity, angle;
	boolean clockwise;

	public Entity(EventLogger l, double v, boolean cw) {
		logger  = l;
		logger.flow("Entity init.");
		velocity = v;
		clockwise = cw;
		angle = 0.0;
	}

	public void update(double spu) {
		if (clockwise)
			angle += velocity * spu;
		else
			angle -= velocity * spu;
	}

	public void display(double interpol) {
		double prediction = ( velocity * interpol );
	}
}
