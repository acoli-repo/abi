package enums;

public enum Column {
	NR (0, null),
	TOK (1, null),
	LEMMA (2, null),
	LEMMA2 (3, null),
	POS (4, null),
	POS_EXT (5, null),
	EMPTY (6, null),
	MORPH (7, null),
	HEAD (8, null),
	HEAD2 (9, null),
	REL (10, null),
	REL2 (11, null),
	IS_PRED (12, null),
	PRED (13, null),
	ARGS (14, null),
	TEMPUS (15, Tempus.values()),
	MODUS (16, Modus.values()),
	ASPEKT (17, Aspekt.values()),
	DIATHESE (18, Diathese.values()),
	MODALITY (19, Modality.values()),
	SATZART (20, Satzart.values()),//IOBES-Schema
	SPRECHAKT(21, Sprechakt.values()),//IOBES-Schema
	REDE (22, Rede.values());//IOBES-Schema

	private final int index;
	private final Enum [] vals;
	Column(int i, Enum [] v){
		this.index = i;
		this.vals = v;
	}
	public Enum [] vals(){
		return this.vals;
	}
	public int getIndex(){
		return this.index;
	}








}
