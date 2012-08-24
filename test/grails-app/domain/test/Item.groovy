package test

class Item {
	String name
	Float price
	Integer quantity
    static constraints = {
		name(nullable: false, blank: false, size:5..50)
		price(nullable: false, min: 0.0f)
		quantity(nullable: false, min: 0)
    }
}
