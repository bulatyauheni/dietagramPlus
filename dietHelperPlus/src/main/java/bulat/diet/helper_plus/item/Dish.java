package bulat.diet.helper_plus.item;

public class Dish implements Comparable<Dish>{
	
	private String id;
	private String name;
	private String description;
	private String categoryName;
	private int caloricity;
	private int weight;
	private int category;
	private float fat;
	private float carbon;
	private float protein;
	private String fatStr;
	private String carbonStr;
	private String proteinStr;
	private String barcode="";
	
	private int iscategory;
	private int popularity;
	private int limit;
	private String type;
		
	public Dish( String name, int caloricity, int weight) {
		super();
		this.name = name;
		this.weight = weight;
		this.caloricity = caloricity;
		
	}
	public Dish( String name, String description, int caloricity,
			int category, int iscategory, int popularity, String type, float fat, float carbon, float protein) {
		super();
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.iscategory = iscategory;
		this.popularity = popularity;
		this.type = type;
		this.fat = fat;
		this.carbon = carbon;
		this.protein = protein;
	}
	
	public Dish( String name, String description, int caloricity,
			int category, int iscategory, int popularity, String type, String fat, String carbon, String protein) {
		super();
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.iscategory = iscategory;
		this.popularity = popularity;
		this.type = type;
		this.fatStr = fat;
		this.carbonStr = carbon;
		this.proteinStr = protein;
	}
	public Dish( String name, String description, int caloricity,
			int category, int iscategory, int popularity, String type, String fat, String carbon, String protein,  String categoryName, String barcode) {
		super();
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.iscategory = iscategory;
		this.popularity = popularity;
		this.type = type;
		this.fatStr = fat;
		this.carbonStr = carbon;
		this.proteinStr = protein;
		this.barcode = barcode;
		this.setCategoryName(categoryName);
	}
	
	public Dish( String name, String description, int caloricity,
			int category, int iscategory, int popularity, String type, String fat, String carbon, String protein,  String categoryName, String barcode, String dishId) {
		super();
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.iscategory = iscategory;
		this.popularity = popularity;
		this.type = type;
		this.fatStr = fat;
		this.carbonStr = carbon;
		this.proteinStr = protein;
		this.barcode = barcode;
		this.setCategoryName(categoryName);
		this.id = dishId;
	}
	
	public Dish( String name, String description, int caloricity,
			int category, int iscategory, int popularity, String type, String fat, String carbon, String protein,  String categoryName) {
		super();
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.iscategory = iscategory;
		this.popularity = popularity;
		this.type = type;
		this.fatStr = fat;
		this.carbonStr = carbon;
		this.proteinStr = protein;
		this.setCategoryName(categoryName);
	}
	
	public Dish( String name, String description, int caloricity,
			int category, int iscategory, int popularity, String type) {
		super();
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.iscategory = iscategory;
		this.popularity = popularity;
		this.type = type;
	}
	
	
	
	
	public Dish(String id, String name, String description, int caloricity,
			int category, float fat, float carbon, float protein) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.fat = fat;
		this.carbon = carbon;
		this.protein = protein;
	}

	public Dish(String id, String name, String description, int caloricity,
			int category, String fat, String carbon, String protein,  String categoryName) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.fatStr = fat;
		this.carbonStr = carbon;
		this.proteinStr = protein;
		this.setCategoryName(categoryName);
	}
	public Dish(String id, String name, String description, int caloricity,
			int category, String fat, String carbon, String protein,  String categoryName, String barcode) {
		super();
		this.id = id;
		this.name = name;
		this.description = description;
		this.caloricity = caloricity;
		this.category = category;
		this.fatStr = fat;
		this.carbonStr = carbon;
		this.proteinStr = protein;
		this.barcode = barcode;
		this.setCategoryName(categoryName);
	}
	
	public Dish() {
		// TODO Auto-generated constructor stub
	}

	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public int getIscategory() {
		return iscategory;
	}

	public void setIscategory(int iscategory) {
		this.iscategory = iscategory;
	}

	public int getCategory() {
		return category;
	}
	public void setCategory(int category) {
		this.category = category;
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public int getCaloricity() {
		return caloricity;
	}
	public void setCaloricity(int caloricity) {
		this.caloricity = caloricity;
	}


	public int getPopularity() {
		return popularity;
	}


	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}


	@Override
	public boolean equals(Object o) {
		try{
		Dish temp = (Dish) o;
		if(this.name.equals(temp.getName()) && this.caloricity==temp.getCaloricity() && this.type.equals(temp.getType())){
			return true;
		}else{
			return false;
		}
		}catch (Exception e) {
			e.printStackTrace();
		}		
		return false;
	}


	public int compareTo(Dish another) {
		try{
			if(another.getName().equals(this.getName())){
				return 0;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 1;
	}


	public int getWeight() {
		return weight;
	}


	public void setWeight(int weight) {
		this.weight = weight;
	}

	public int getLimit() {
		return limit;
	}

	public void setLimit(int limit) {
		this.limit = limit;
	}

	public float getFat() {
		return fat;
	}

	public void setFat(float fat) {
		this.fat = fat;
	}

	public float getCarbon() {
		return carbon;
	}

	public void setCarbon(float carbon) {
		this.carbon = carbon;
	}

	public float getProtein() {
		return protein;
	}

	public void setProtein(float protein) {
		this.protein = protein;
	}
	public String getProteinStr() {
		return proteinStr;
	}
	public void setProteinStr(String proteinStr) {
		this.proteinStr = proteinStr;
	}
	public String getCarbonStr() {
		return carbonStr;
	}
	public void setCarbonStr(String carbonStr) {
		this.carbonStr = carbonStr;
	}
	public String getFatStr() {
		return fatStr;
	}
	public void setFatStr(String fatStr) {
		this.fatStr = fatStr;
	}
	public String getCategoryName() {
		return categoryName;
	}
	public void setCategoryName(String categoryName) {
		this.categoryName = categoryName;
	}
	public String getBarcode() {
		return barcode;
	}
	public void setBarcode(String barcode) {
		this.barcode = barcode;
	}
			

}
