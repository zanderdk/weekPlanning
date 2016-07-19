(function ($) {
    $.fn.extend({
        cookieList: function (cookieName) {
            var cookie = $.cookie(cookieName);

            var items = cookie ? eval("([" + cookie + "])") : [];

            return {
                add: function (val) {
                    var index = items.indexOf(val);

                    // Note: Add only unique values.
                    if (index == -1) {
                        items.push(val);
                        $.cookie(cookieName, items.join(','), { expires: 365, path: '/' });
                    }
                },
                remove: function (val) {
                    var index = items.indexOf(val);

                    if (index != -1) {
                        items.splice(index, 1);
                        $.cookie(cookieName, items.join(','), { expires: 365, path: '/' });
                     }
                },
                indexOf: function (val) {
                    return items.indexOf(val);
                },
                clear: function () {
                    items = null;
                    $.cookie(cookieName, null, { expires: 365, path: '/' });
                },
                items: function () {
                    return items;
                },
                length: function () {
                    return items.length;
                },
                join: function (separator) {
                    return items.join(separator);
                }
            };
        }
    });
})(jQuery);

Array.prototype.max = function() {
  return Math.max.apply(null, this);
};

Array.prototype.min = function() {
  return Math.min.apply(null, this);
};

function flatten(array, mutable) {
    var toString = Object.prototype.toString;
    var arrayTypeStr = '[object Array]';

    var result = [];
    var nodes = (mutable && array) || array.slice();
    var node;

    if (!array.length) {
        return result;
    }

    node = nodes.pop();

    do {
        if (toString.call(node) === arrayTypeStr) {
            nodes.push.apply(nodes, node);
        } else {
            result.push(node);
        }
    } while (nodes.length && (node = nodes.pop()) !== undefined);

    result.reverse(); // we reverse result to restore the original order
    return result;
}
