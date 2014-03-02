

function sendServerRequest(requestUrl, onSucessFunction)
{
	if ((typeof requestUrl !== 'undefined') && (requestUrl !== null) && (requestUrl.length > 0))
	{
		if ((typeof onSucessFunction !== 'undefined') && (onSucessFunction !== null) && (typeof onSucessFunction === 'function'))
		{
			var xmlhttp;
			if (window.XMLHttpRequest)
			{
				// code for IE7+, Firefox, Chrome, Opera, Safari
				xmlhttp = new XMLHttpRequest();
			}
			else
			{
				// code for IE6, IE5
				xmlhttp = new ActiveXObject("Microsoft.XMLHTTP");
			}
			xmlhttp.onreadystatechange = function()
			{
				if ((xmlhttp.readyState === 4) && (xmlhttp.status === 200))
				{
					if ((typeof onSucessFunction !== 'undefined') && (onSucessFunction !== null) && (typeof onSucessFunction === 'function'))
					{
						onSucessFunction(xmlhttp.responseText);
					}
				}
			}

			xmlhttp.open("GET", requestUrl, true);
			xmlhttp.send();
		}
	}
};


var renterGetCities_onkeyUp_index = 0;
var renterGetCities_scgCode5 = 0;
var renterGetCities_scgCode7 = 0;

function renterGetCities_onkeyUp()
{
	renterGetCities_scgCode5 = 0;
	renterGetCities_scgCode7 = 0;
	var renterCityInput = document.getElementById('renterLocationName');
	if ((typeof renterCityInput !== 'undefined') && (renterCityInput !== null))
	{
		var renterCityInputValue = renterCityInput.value;
		if ((typeof renterCityInputValue !== 'undefined') && (renterCityInputValue !== null) && (renterCityInputValue.length >= 3))
		{
			renterGetCities_onkeyUp_index++;
			var onkeyUp_index = renterGetCities_onkeyUp_index;
			setTimeout(function(){
				if (renterGetCities_onkeyUp_index === onkeyUp_index)
				{
					sendServerRequest('api/places?name=' + renterCityInputValue, function(responseText){
						if (renterGetCities_onkeyUp_index === onkeyUp_index)
						{
							var citiesResponse = JSON.parse(responseText);
							if ((typeof citiesResponse !== 'undefined') && (citiesResponse !== null))
							{
								if (citiesResponse.count > 0)
								{
									var placesStr = '';
									for (var n=0; n<citiesResponse.places.length; n++)
									{
										renterCityInput.value = citiesResponse.places[n].name;
										renterGetCities_scgCode5 = citiesResponse.places[n].scgCode5;
										renterGetCities_scgCode7 = citiesResponse.places[n].scgCode7;
										break;
									}
								}
							}
						}
					});
				}
			}, 500);
		}
	}
};

var renterGetRentValues_waiting = false;

function renterGetRentValues()
{
	if (renterGetCities_scgCode7 !== 0)
	{
		if (renterGetRentValues_waiting === false)
		{
			renterGetRentValues_waiting = true;
			sendServerRequest('api/renter/result?scgCode5=' + renterGetCities_scgCode7, function(responseText){
				renterGetRentValues_waiting = false;
				var rentalRatesResponse = JSON.parse(responseText);
				if ((typeof rentalRatesResponse !== 'undefined') && (rentalRatesResponse !== null))
				{
					if (rentalRatesResponse.yearCount > 0)
					{
						var placesStr = 'Year: ' + rentalRatesResponse.years[0].year;
						for (var n=0; n<rentalRatesResponse.years[0].rentalRates.length; n++)
						{
							placesStr += '<br />';
							placesStr += 'Rental Index: ' + (n + 1);
							placesStr += '<br />';
							placesStr += 'Building Type: ' + rentalRatesResponse.years[0].rentalRates[n].buildingType;
							placesStr += '<br />';
							placesStr += 'Building Unit Type: ' + rentalRatesResponse.years[0].rentalRates[n].unitType;
							placesStr += '<br />';
							placesStr += 'Rental Rate: $' + rentalRatesResponse.years[0].rentalRates[n].rentalRate;
							placesStr += '<br />';
						}
						var renterRentalRatesDiv = document.getElementById('renterRentalRatesDiv');
						if ((typeof renterRentalRatesDiv !== 'undefined') && (renterRentalRatesDiv !== null))
						{
							renterRentalRatesDiv.innerHTML = placesStr;
						}
					}
				}
			});
		}
	}
	else
	{
		alert('be patient ...');
	}
};




var buyerGetCities_onkeyUp_index = 0;
var buyerGetCities_scgCode5 = 0;
var buyerGetCities_scgCode7 = 0;
var buyerGetCities_price = -1;
var buyerGetCities_yearOfPurchase = 2007;

function buyerGetCities_onkeyUp()
{
	buyerGetCities_scgCode5 = 0;
	buyerGetCities_scgCode7 = 0;
	var cityInput = document.getElementById('buyerLocationName');
	if ((typeof cityInput !== 'undefined') && (cityInput !== null))
	{
		var cityInputValue = cityInput.value;
		if ((typeof cityInputValue !== 'undefined') && (cityInputValue !== null) && (cityInputValue.length >= 3))
		{
			buyerGetCities_onkeyUp_index++;
			var onkeyUp_index = buyerGetCities_onkeyUp_index;
			setTimeout(function(){
				if (buyerGetCities_onkeyUp_index === onkeyUp_index)
				{
					sendServerRequest('api/places?name=' + cityInputValue, function(responseText){
						if (buyerGetCities_onkeyUp_index === onkeyUp_index)
						{
							var citiesResponse = JSON.parse(responseText);
							if ((typeof citiesResponse !== 'undefined') && (citiesResponse !== null))
							{
								if (citiesResponse.count > 0)
								{
									var placesStr = '';
									for (var n=0; n<citiesResponse.places.length; n++)
									{
										cityInput.value = citiesResponse.places[n].name;
										buyerGetCities_scgCode5 = citiesResponse.places[n].scgCode5;
										buyerGetCities_scgCode7 = citiesResponse.places[n].scgCode7;
										break;
									}
								}
							}
						}
					});
				}
			}, 500);
		}
	}
};

function buyerSetPrice()
{
	buyerGetCities_price = -1;
	var housePrice = document.getElementById('buyerHousePrice');
	if ((typeof housePrice !== 'undefined') && (housePrice !== null))
	{
		var housePriceValue = housePrice.value;
		if ((typeof housePriceValue !== 'undefined') && (housePriceValue !== null) && (housePriceValue.length >= 0))
		{
			buyerGetCities_price = parseFloat(housePriceValue);
		}
		else
		{
			housePrice.value = '';
		}
	}
};

function buyerSetYear()
{
	buyerGetCities_yearOfPurchase = -1;
	var purchaseYear = document.getElementById('buyerPurchaseYear');
	if ((typeof purchaseYear !== 'undefined') && (purchaseYear !== null))
	{
		var purchaseYearValue = purchaseYear.value;
		if ((typeof purchaseYearValue !== 'undefined') && (purchaseYearValue !== null) && (purchaseYearValue.length >= 3))
		{
			buyerGetCities_yearOfPurchase = parseInt(purchaseYearValue);
		}
		else
		{
			purchaseYear.value = '';
		}
	}
};

var buyerGetRentValues_waiting = false;

function buyerGetIndexHouseValues()
{
	if (((buyerGetCities_scgCode5 > 0) || (buyerGetCities_scgCode7 > 0)) && (buyerGetCities_yearOfPurchase > 0) && (buyerGetCities_price > 0.0))
	{
		if (buyerGetRentValues_waiting === false)
		{
			buyerGetRentValues_waiting = true;
			sendServerRequest('api/buyer/result?scgCode5=' + buyerGetCities_scgCode5 + '&scgCode7=' + buyerGetCities_scgCode7 + '&price=' + buyerGetCities_price + '&yearOfPurchase=' + buyerGetCities_yearOfPurchase, function(responseText){
				renterGetRentValues_waiting = false;
				var indexHousePriceResponse = JSON.parse(responseText);
				if ((typeof indexHousePriceResponse !== 'undefined') && (indexHousePriceResponse !== null))
				{
					var buyerRentalRatesDiv = document.getElementById('buyerRentalRatesDiv');
					if ((typeof buyerRentalRatesDiv !== 'undefined') && (buyerRentalRatesDiv !== null))
					{
						buyerRentalRatesDiv.innerHTML = JSON.stringify(indexHousePriceResponse);
					}
				}
			});
		}
	}
	else
	{
		alert('be patient ...');
	}
};