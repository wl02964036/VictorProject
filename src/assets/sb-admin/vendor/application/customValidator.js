(function(window,$) {

    /* 檢查是否為英文 */
    window.Parsley.addValidator("englishOnly", {
        validateString: function (value, requirement, instance) {
            var regex = /^[a-zA-Z]+$/;
            return regex.test(value);
        },
        messages: { "zh-tw": "這個欄位只接受英文字母"}
    });

    /* 檢查是否為英文和數字 */
    window.Parsley.addValidator("englishNumberOnly", {
        validateString: function (value, requirement, instance) {
            var regex = /^[a-zA-Z0-9]+$/;
            return regex.test(value);
        },
        messages: { "zh-tw": "這個欄位只接受英文字母或是數字"}
    });

    /* 檢查是否為身分證統一編號 */
    window.Parsley.addValidator("identityUniformId", {
        validateString: function (value, requirement, instance) {
            var validated = true;
            var id = value.toUpperCase();

            if (id.search(/^[A-Z][1-2]\d{8}$/i) != -1) {
                var tab = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
                var A1 = new Array (1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3 );
                var A2 = new Array (0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5 );
                var Mx = new Array (9,8,7,6,5,4,3,2,1,1);

                var i = tab.indexOf( id.charAt(0) );
                var sum = A1[i] + A2[i]*9;

                var v;
                for ( i=1; i<10; i++ ) {
                  v = parseInt( id.charAt(i) );
                  sum = sum + v * Mx[i];
                }

                if ( sum % 10 != 0 ) {
                  validated = false;
                } else {
                  validated = true;
                }
            } else {
                validated = false;
            }

            return validated;
        },
        messages: { "zh-tw": "不正確的身分證統一編號"}
    });

    /* 檢查是否為舊居留證統一編號 */
    window.Parsley.addValidator("residentUniformId", {
        validateString: function (value, requirement, instance) {
            var validated = true;
            var id = value.toUpperCase();

            if (id.search(/^[A-Z][A-D][0-9]{8}$/i) != -1) {
                var sum = 0;
                var str1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                var str2 = "1011121314151617341819202122352324252627282932303133";
                var t1 = str2.substr(str1.indexOf(id.substr(0,1)) * 2 ,2);
                var t2 = str2.substr(str1.indexOf(id.substr(1,1)) * 2 ,2);

                sum = t1.substr(0,1) * 1 + t1.substr(1,1) * 9;
                sum += (t2 % 10 ) * 8;

                var t10 = id.substr(9,1);

                for (var t_i = 3; t_i <= 9; t_i++) {
                    sum += id.substr(t_i-1,1) * (10 - t_i);
                }

                var t10_;
                (sum % 10 == 0 ) ? t10_ = 0 : t10_ = 10 - (sum % 10);

                if (t10_ == t10 ) {
                    validated = true;
                } else {
                    validated = false;
                }
            } else {
                validated = false;
            }

            return validated;
        },
        messages: { "zh-tw": "不正確的舊式居留證統一編號"}
    });

    /* 檢查是否為新居留證統一編號 */
    window.Parsley.addValidator("modernResidentUniformId", {
        validateString: function (value, requirement, instance) {
            var validated = true;
            var id = value.toUpperCase();

            if (id.search(/^[A-Z][8-9]\d{8}$/i) != -1) {
                var tab = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
                var A1 = new Array (1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3 );
                var A2 = new Array (0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5 );
                var Mx = new Array (9,8,7,6,5,4,3,2,1,1);

                var i = tab.indexOf( id.charAt(0) );
                var sum = A1[i] + A2[i]*9;

                var v;
                for ( i=1; i<10; i++ ) {
                    v = parseInt( id.charAt(i) );
                    sum = sum + v * Mx[i];
                }

                if ( sum % 10 != 0 ) {
                    validated = false;
                } else {
                    validated = true;
                }
            } else {
                validated = false;
            }

            return validated;
        },
        messages: { "zh-tw": "不正確的新式居留證統一編號"}
    });

    /* 檢查是否為身分證或新舊居留證統一編號 */
    window.Parsley.addValidator("uniformId", {
        validateString: function (value, requirement, instance) {
            var validated = true;
            var id = value.toUpperCase();

            if (id.search(/^[A-Z][1-2]\d{8}$/i) != -1) {
                // 身份證號
                var tab = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
                var A1 = new Array (1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3 );
                var A2 = new Array (0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5 );
                var Mx = new Array (9,8,7,6,5,4,3,2,1,1);

                var i = tab.indexOf( id.charAt(0) );
                var sum = A1[i] + A2[i]*9;

                var v;
                for ( i=1; i<10; i++ ) {
                    v = parseInt( id.charAt(i) );
                    sum = sum + v * Mx[i];
                }

                if ( sum % 10 != 0 ) {
                    validated = false;
                } else {
                    validated = true;
                }

            } else if (id.search(/^[A-Z][A-D][0-9]{8}$/i) != -1) {
                // 舊居留證號
                var sum = 0;
                var str1 = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
                var str2 = "1011121314151617341819202122352324252627282932303133";
                var t1 = str2.substr(str1.indexOf(id.substr(0,1)) * 2 ,2);
                var t2 = str2.substr(str1.indexOf(id.substr(1,1)) * 2 ,2);

                sum = t1.substr(0,1) * 1 + t1.substr(1,1) * 9;
                sum += (t2 % 10 ) * 8;

                var t10 = id.substr(9,1);

                for (var t_i = 3; t_i <= 9; t_i++) {
                    sum += id.substr(t_i-1,1) * (10 - t_i);
                }

                var t10_;
                (sum % 10 == 0 ) ? t10_ = 0 : t10_ = 10 - (sum % 10);

                if (t10_ == t10 ) {
                    validated = true;
                } else {
                    validated = false;
                }
            } else if (id.search(/^[A-Z][8-9]\d{8}$/i) != -1) {
                // 新居留證號
                var tab = "ABCDEFGHJKLMNPQRSTUVXYWZIO";
                var A1 = new Array (1,1,1,1,1,1,1,1,1,1,2,2,2,2,2,2,2,2,2,2,3,3,3,3,3,3 );
                var A2 = new Array (0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5,6,7,8,9,0,1,2,3,4,5 );
                var Mx = new Array (9,8,7,6,5,4,3,2,1,1);

                var i = tab.indexOf( id.charAt(0) );
                var sum = A1[i] + A2[i]*9;

                var v;
                for ( i=1; i<10; i++ ) {
                    v = parseInt( id.charAt(i) );
                    sum = sum + v * Mx[i];
                }

                if ( sum % 10 != 0 ) {
                    validated = false;
                } else {
                    validated = true;
                }
            } else {
                validated = false;
            }

            return validated;
        },
        messages: { "zh-tw": "不正確的身分證統一編號/居留證統一編號"}
    });

    /* 檢查生日日期格式 */
    window.Parsley.addValidator("birthdate", {
        validateString: function (value, requirement, instance) {
            return /^[0-9]{4}-[0-9]{2}-[0-9]{2}$/.test(value);
        },
        messages: { "zh-tw": "不正確的生日日期格式"}
    });

    /* 檢查手機號碼格式, notRequired 為 true 的話，空字串可以過 */
    window.Parsley.addValidator("mobile", {
        requirementType: 'boolean',
        validateString: function (value, notRequired, instance) {
            if (notRequired && value === "") {
                return true;
            } else {
                return /^[09]{2}[0-9]{8}$/.test(value);
            }
        },
        messages: { "zh-tw": "不正確的行動電話格式"}
    });

    /* 檢查市話格式, notRequired 為 true 的話，空字串可以過 */
    window.Parsley.addValidator("phone", {
        requirementType: 'boolean',
        validateString: function (value, notRequired, instance) {
            if (notRequired && value === "") {
                return true;
            } else {
                return /^[0-9]{2,4}-\d{7,8}(#\d{1,6})?$/.test(value);
            }
        },
        messages: { "zh-tw": "不正確的市區電話格式"}
    });

    /* 檢查自己或另一個欄位是不是有任一填值 */
    window.Parsley.addValidator("anyone", {
        requirementType: 'string',
        validateString: function (value, requirement, instance) {
            var selfValue = value.trim();
            var otherValue = $(requirement).val().trim();

            if (selfValue !== "" || otherValue !== "") {
                instance.reset();
                return true;
            } else {
                return false;
            }
        },
        messages: { "zh-tw": "兩個項目請至少填一項"}
    });

    /* 檢查Citizen密碼是否符合規則 */
    window.Parsley.addValidator("citizenPwd", {
        validateString: function (value, requirement, instance) {
            return /^(?=.*\d)(?=.*[a-z])(?=.*[A-Z]).{8,}$/.test(value);
        },
        messages: { "zh-tw": "密碼需八碼以上(含8碼)，並且須包含英文大寫字母，英文小寫字母與數字"}
    });

    /* 客製化檢查email格式, notRequired 為 true 的話，空字串可以過 */
    window.Parsley.addValidator("emailCustom", {
        requirementType: 'boolean',
        validateString: function (value, notRequired, instance) {
            if (notRequired && value === "") {
                return true;
            } else {
                return /^\w+((-\w+)|(\.\w+))*\@[A-Za-z0-9]+((\.|-)[A-Za-z0-9]+)*\.[A-Za-z]+$/.test(value);
            }
        },
        messages: { "zh-tw": "請輸入一個正確的電子郵件地址"}
    });

})(window, jQuery);
