import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import React, { useEffect, useState } from "react";
import Navbar from "../../../components/NavbarUser";
import { faCircleXmark } from "@fortawesome/free-regular-svg-icons";
import Swal from "sweetalert2";
import axios from "axios";
import { Pagination } from "flowbite-react";
import { API_DUMMY } from "../../../utils/api";
import SidebarNavbar from "../../../components/SidebarNavbar";

function TabelCuti() {
  const [cuti, setCuti] = useState([]);
  const [searchTerm, setSearchTerm] = useState("");
  const [limit, setLimit] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);
  const [sidebarOpen, setSidebarOpen] = useState(false);

  const toggleSidebar = () => {
    setSidebarOpen(!sidebarOpen);
  };

  const formatDate = (dateString) => {
    const options = {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    };
    return new Date(dateString).toLocaleDateString("id-ID", options);
  };

  const getAllCuti = async () => {
    const token = localStorage.getItem("token");
    const userId = localStorage.getItem("userId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/cuti/getByUser/${userId}`,
        {
          headers: {
            Authorization: `${token}`,
          },
        }
      );

      setCuti(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const BatalCuti = async (id) => {
    const token = localStorage.getItem("token");

    await Swal.fire({
      title: "Anda yakin?",
      text: "Yakin ingin membatalkan cuti ini?",
      icon: "warning",
      showCancelButton: true,
      confirmButtonColor: "#3085d6",
      cancelButtonColor: "#d33",
      confirmButtonText: "Ya",
      cancelButtonText: "Batal",
    }).then((result) => {
      if (result.isConfirmed) {
        axios
          .delete(`${API_DUMMY}/api/cuti/delete/${id}`, {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          })
          .then(() => {
            Swal.fire({
              position: "center",
              icon: "success",
              title: "Berhasil Menghapus!!",
              showConfirmButton: false,
              timer: 1500,
            }).then(() => {
              window.location.reload();
            });
          })
          .catch((error) => {
            console.error("Error deleting data:", error);
          });
      }
    });
  };

  useEffect(() => {
    getAllCuti();
  }, []);

  useEffect(() => {
    const filteredData = cuti.filter(
      (cutiData) =>
        (cutiData.keperluan?.toLowerCase().includes(searchTerm.toLowerCase()) ??
          false) ||
        (cutiData.status?.toLowerCase().includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(cutiData.awalCuti)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(cutiData.akhirCuti)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false) ||
        (formatDate(cutiData.masukKerja)
          ?.toLowerCase()
          .includes(searchTerm.toLowerCase()) ??
          false)
    );
    setTotalPages(Math.ceil(filteredData.length / limit));
  }, [searchTerm, limit, cuti]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleLimitChange = (event) => {
    setLimit(parseInt(event.target.value));
    setCurrentPage(1); // Reset to the first page when limit changes
  };

  function onPageChange(page) {
    setCurrentPage(page);
  }

  const filteredCuti = cuti.filter(
    (cutiData) =>
      (cutiData.keperluan?.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (cutiData.status?.toLowerCase().includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(cutiData.awalCuti)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(cutiData.akhirCuti)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false) ||
      (formatDate(cutiData.masukKerja)
        ?.toLowerCase()
        .includes(searchTerm.toLowerCase()) ??
        false)
  );

  const paginatedCuti = filteredCuti.slice(
    (currentPage - 1) * limit,
    currentPage * limit
  );

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <SidebarNavbar />
      </div>
      <div className="flex h-full">
        <div className="sticky top-16 z-40">
          <Navbar />
        </div>
        <div className="content-page flex-1 p-8 md:ml-64 mt-16">
          <div className="tabel-cuti bg-blue-100 p-5 rounded-xl shadow-xl border border-gray-300 text-center">
            <div className="flex justify-between">
              <h2 className="text-xl font-bold">History Cuti</h2>
              <div className="flex items-center gap-2 mt-2">
                <div className="relative w-64">
                  <input
                    type="search"
                    id="search-dropdown"
                    value={searchTerm}
                    onChange={handleSearch}
                    className="block p-2.5 w-full z-20 text-sm rounded-l-md text-gray-900 bg-gray-50 border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:border-blue-500"
                    placeholder="Search name..."
                    required
                  />
                </div>
                <select
                  value={limit}
                  onChange={handleLimitChange}
                  className="flex-shrink-0 z-10 inline-flex rounded-r-md items-center py-2.5 px-4 text-sm font-medium text-gray-900 bg-gray-100 border border-gray-300 hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600"
                >
                  <option value="5">05</option>
                  <option value="10">10</option>
                  <option value="20">20</option>
                  <option value="50">50</option>
                </select>
              </div>
            </div>

            <div className="overflow-x-auto rounded-xl border border-gray-200 mt-4">
              <table className="min-w-full divide-y-2 divide-gray-200 bg-white text-sm border border-gray-300">
                <thead className="text-left text-white bg-blue-500">
                  <tr>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      NO
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      CUTI DARI
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      SAMPAI
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      MASUK KERJA
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      KEPERLUAN
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      STATUS
                    </th>
                    <th className="whitespace-nowrap px-4 py-2 font-medium text-center">
                      AKSI
                    </th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-gray-200">
                  {paginatedCuti.map((cutiData, index) => (
                    <tr key={index}>
                      <td className="whitespace-nowrap px-4 py-2 font-medium text-gray-900 text-center">
                        {(currentPage - 1) * limit + index + 1}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {formatDate(cutiData.awalCuti)}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {formatDate(cutiData.akhirCuti)}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {formatDate(cutiData.masukKerja)}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {cutiData.keperluan}
                      </td>
                      <td className="whitespace-nowrap px-4 py-2 text-gray-700 text-center capitalize">
                        {cutiData.status}
                      </td>
                      <td className="whitespace-nowrap text-center py-3">
                        <div className="flex items-center -space-x-4 ml-12">
                          <button
                            className="z-20 block rounded-full border-2 border-white bg-red-100 p-4 text-red-700 active:bg-blue-50"
                            onClick={() => BatalCuti(cutiData.id)} // Pemanggilan fungsi saat tombol ditekan dengan meneruskan ID cuti
                          >
                            <span className="relative inline-block">
                              <FontAwesomeIcon
                                icon={faCircleXmark}
                                className="h-4 w-4"
                              />
                            </span>
                          </button>
                        </div>
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
            <Pagination
              className="mt-5"
              layout="table"
              currentPage={currentPage}
              totalPages={totalPages}
              onPageChange={onPageChange}
              showIcons
            />
          </div>
        </div>
      </div>
    </div>
  );
}

export default TabelCuti;
