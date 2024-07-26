import React, { useEffect, useState } from "react";
import Logo from "../components/absensii.png";
import { Link } from "react-router-dom/cjs/react-router-dom.min";
import axios from "axios";
import { API_DUMMY } from "../utils/api";
import Swal from "sweetalert2";
const Navbar = ({ toggleSidebar }) => {
  const [profileUser, setProfileUser] = useState("");
  const [userMenuOpen, setUserMenuOpen] = useState(false);

  const role = localStorage.getItem("role");

  const getUser = async () => {
    const id = localStorage.getItem("userId");
    try {
      const user = await axios.get(`${API_DUMMY}/api/user/getUserBy/${id}`);
      setProfileUser(user.data.fotoUser);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getUser();
  });

  const toggleUserMenu = () => {
    setUserMenuOpen(!userMenuOpen);
  };

  function logout() {
    // Tampilkan SweetAlert2 konfirmasi sebelum logout
    Swal.fire({
      title: "Logout",
      text: "Apakah Anda yakin ingin logout?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Ya, Logout",
      cancelButtonText: "Batal",
    }).then((result) => {
      if (result.isConfirmed) {
        // Hapus item dari local storage saat logout
        localStorage.clear();
        Swal.fire({
          title: "Logout Berhasil",
          text: "Anda telah berhasil logout.",
          icon: "success",
          showConfirmButton: false,
          timer: 1000,
        }).then(() => {
          window.location.href = "/";
        });
      }
    });
  }
  
  return (
    <nav className="fixed top-0 z-50 w-full bg-indigo-500 border-b border-gray-200 dark:bg-gray-800 dark:border-gray-700">
      <div className="px-3 py-3 lg:px-5 lg:pl-3">
        <div className="flex items-center justify-between">
          <div className="flex items-center justify-start rtl:justify-end">
            <button
              data-drawer-target="logo-sidebar"
              data-drawer-toggle="logo-sidebar"
              aria-controls="logo-sidebar"
              type="button"
              onClick={toggleSidebar}
              className="inline-flex items-center p-2 text-sm text-gray-500 rounded-lg sm:hidden hover:bg-gray-100 focus:outline-none focus:ring-2 focus:ring-gray-200 dark:text-gray-400 dark:hover:bg-indigo-500 dark:focus:ring-gray-600"
            >
              <span className="sr-only">Open sidebar</span>
              <svg
                className="w-6 h-6"
                aria-hidden="true"
                fill="currentColor"
                viewBox="0 0 20 20"
                xmlns="http://www.w3.org/2000/svg"
              >
                <path
                  clipRule="evenodd"
                  fillRule="evenodd"
                  d="M2 4.75A.75.75 0 012.75 4h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 4.75zm0 10.5a.75.75 0 01.75-.75h7.5a.75.75 0 010 1.5h-7.5a.75.75 0 01-.75-.75zM2 10a.75.75 0 01.75-.75h14.5a.75.75 0 010 1.5H2.75A.75.75 0 012 10z"
                ></path>
              </svg>
            </button>
            <a href="" className="flex ms-2 md:me-24">
              <img src={Logo} className="h-11 me-3 text-white" alt="" />
              <span className="self-center text-xl font-semibold sm:text-2xl whitespace-nowrap dark:text-white"></span>
            </a>
          </div>
          <div className="flex items-center">
            <div className="flex items-center ms-3">
              <div>
                <button
                  type="button"
                  className="flex text-sm bg-gray-800 rounded-full focus:ring-4 focus:ring-gray-300 dark:focus:ring-gray-600"
                  onClick={toggleUserMenu}
                  id="user-menu-button"
                  aria-expanded={userMenuOpen}
                  aria-haspopup="true"
                >
                  <span className="sr-only">Open user menu</span>
                  <img
                    className="w-8 h-8 rounded-full"
                    src={
                      profileUser
                        ? profileUser
                        : "data:image/jpeg;base64,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"
                    }
                    alt="user photo"
                  />
                </button>
                {userMenuOpen && (
                  <div
                    role="menu"
                    aria-orientation="vertical"
                    aria-labelledby="user-menu-button"
                    className="absolute right-0 mt-2 w-48 bg-white dark:bg-gray-800 rounded-md shadow-lg py-1"
                    tabIndex="-1"
                  >
                    <Link to="/user/profile">
                      <button
                        role="menuitem"
                        tabIndex="-1"
                        id="user-menu-item-0"
                        className="block px-4 py-2 text-sm text-gray-700 dark:text-white"
                      >
                        Profile
                      </button>
                    </Link>
                    <button
                      onClick={() =>  logout()}
                      role="menuitem"
                      tabIndex="-1"
                      id="user-menu-item-2"
                      className="block px-4 py-2 text-sm text-gray-700 dark:text-white w-full text-left"
                    >
                      Keluar
                    </button>
                  </div>
                )}
              </div>
            </div>
          </div>
        </div>
      </div>
    </nav>
  );
};

export default Navbar;
