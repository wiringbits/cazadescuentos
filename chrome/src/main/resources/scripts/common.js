/**
 * Expose functions that are difficult to represent in Scala.
 *
 * The idea is to create a facade that simplifies them to our needs.
 */
var facade = {
  notify: (title, message, iconUrl) => {
    chrome.notifications.create("", { title: title, message: message, type: "basic", iconUrl: iconUrl })
  },
  isPopUpMobileResolution: () => {
    const MAX_SIZE = "576";
    let divTransparent = document.createElement("div");
    divTransparent.id = "test-SizeWindows";
    divTransparent.style.width = "800px";
    divTransparent.style.height = "1px";
    divTransparent.style.visibility = "hidden";
    document.body.appendChild(divTransparent)
    let currentWindowSize = Math.max(document.documentElement.clientWidth || 0, window.innerWidth || 0)
    document.getElementsByTagName("body")[0].removeChild(document.getElementById("test-SizeWindows"))
    console.log(currentWindowSize)
    return (currentWindowSize <= MAX_SIZE)
  }
};

