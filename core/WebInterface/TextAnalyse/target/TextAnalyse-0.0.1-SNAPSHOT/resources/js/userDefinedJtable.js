$(document).ready(function() {
	$('#FeatureTableContainer').jtable({
		title : 'Feature List',
		actions : {
			listAction : 'listAction',
			createAction : 'createAction',
			updateAction : 'updateAction',
			deleteAction : 'deleteAction'
		},

		fields : {
			featureID : {
				title : 'Feature ID',
				width : '20%',
				key : true,
				list : true,
				edit : false,
				create : true
			},
			featurename : {
				title : 'Feature-Name',
				width : '20%',
				edit : true
			},featureWert : {
				title : 'Feature-Wert',
				width : '20%',
				edit : true
			},
			featureform : {
				title : 'Feature-Formel',
				width : '30%',
				edit : true
			}
		}
	});
	$('#FeatureTableContainer').jtable('load');
});
