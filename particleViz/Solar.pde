class Solar extends Particle {

  Solar(PVector l) {
    super(l); // is really necessary.
  }
  // We inherite update() from parent class
  void display() { // Overwrites the display method
    fill(255,0,255);
    ellipse(location.x, location.y, size, size);
  }
}
